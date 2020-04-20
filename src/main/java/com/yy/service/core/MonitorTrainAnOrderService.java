package com.yy.service.core;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yy.constant.TrainOrderStatus;
import com.yy.dao.TrainAnOrderRepository;
import com.yy.dao.TrainTicketRepository;
import com.yy.dao.WxAccountRepository;
import com.yy.dao.entity.*;
import com.yy.domain.Session;
import com.yy.domain.Train;
import com.yy.service.api.API12306Service;
import com.yy.service.util.PriorityService;
import com.yy.service.util.SessionPoolService;
import com.yy.util.TimeFormatUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 查询我的订单（未完成订单、未出行订单、历史订单）
 */
@Service
@EnableScheduling
public class MonitorTrainAnOrderService {

    private static final Logger LOGGER = Logger.getLogger(MonitorTrainAnOrderService.class);
    //28分钟未支付，视为超时
    private static final int TIMEOUT = 28 * 60 * 1000;
    @Autowired
    API12306Service api12306Service;
    @Autowired
    SessionPoolService sessionPoolService;
    @Autowired
    WxAccountRepository wxAccountRepository;
    @Autowired
    TrainTicketRepository trainTicketRepository;
    @Autowired
    Login12306Service login12306Service;
    @Autowired
    TrainAnOrderRepository trainAnOrderRepository;
    @Autowired
    PriorityService priorityService;
    //用户订单->抢到单的时间
    private Map<Task, Long> tasks = new ConcurrentHashMap<>();

    void addTask(UserOrder order, Train train) {
        WxAccount wxAccount = wxAccountRepository.findByOpenId(order.getOpenId());
        Task task = new Task(order.getOrderId(),
                wxAccount.getUsername(), wxAccount.getPassword(),
                train.getTrainCode(), train.getFromStation(), train.getToStation(),
                train.getFromDate(), train.getToDate(), train.getFromTime(), train.getToTime(), train.getDuration());
        //保存订单
        queryAndSaveTrainAnOrder(task);
        //加入定时任务，轮询用户有没有支付
        tasks.put(task, System.currentTimeMillis());
    }

    /**
     * 轮询查看支付状态
     * 如果30分钟内完成支付
     * 则订单更新为已完成
     * 否则更新为超时未支付
     */
    @Async
    @Scheduled(fixedRate = 60000)
    public void updateStatus() {
        for (Task task : tasks.keySet()) {
            long t1 = tasks.get(task);
            //当前时间
            long t2 = System.currentTimeMillis();
            //如果支付超时
            if (t2 - t1 >= TIMEOUT) {
                trainAnOrderRepository.updateStatusByUserOrderId(
                        TrainOrderStatus.PAY_TIMEOUT.getStatus(),
                        task.userOrderId);
                tasks.remove(task);
                //否则，说明仍然未支付
                LOGGER.info(String.format("用户候补订单【%s】支付超时", task.userOrderId));
                continue;
            }
            //如果未超时，查看是否支付
            TrainAnOrder trainAnOrder = queryTrainAnOrder(task);
            if (trainAnOrder == null) {
                //找不到未完成订单，说明已经支付了
                trainAnOrderRepository.updateStatusByUserOrderId(
                        TrainOrderStatus.COMPLETED.getStatus(),
                        task.userOrderId);
                tasks.remove(task);
                //否则，说明仍然未支付
                LOGGER.info(String.format("用户候补订单【%s】已支付", task.userOrderId));
            } else {
                //否则，说明仍然未支付
                LOGGER.info(String.format("用户候补订单【%s】还未支付", task.userOrderId));
            }
        }
    }

    private TrainAnOrder queryTrainAnOrder(Task task) {
        Session session = sessionPoolService.getSession(task.username);
        //检查用户是否登陆成功
        boolean success = api12306Service.checkLogin(session);
        if (!success) {
            login12306Service.login(task.username, task.password);
            priorityService.sleepRandomTime(1000, 2000);
            success = api12306Service.checkLogin(session);
            if (!success) {
                LOGGER.error("queryTrainOrder：登陆失败，获取不到火车订单信息");
                return null;
            }
        }
        JSONObject jsonObject = api12306Service.queryNoCompleteAnOrder(session);
        if (!jsonObject.containsKey("data")) {
            return null;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (!data.containsKey("order")) {
            return null;
        }
        JSONObject order = data.getJSONObject("order");
        JSONArray needs = order.getJSONArray("needs");
        if (needs.size() != 1) {
            LOGGER.info("queryTrainAnOrder:候补车票信息错误");
            return null;
        }
        String reserveNo = order.getString("reserve_no");
        String prepayAmount = order.getString("prepay_amount");
        String status = order.getString("status_name");

        JSONObject need = needs.getJSONObject(0);
        String seatType = need.getString("seat_name");
        String queueInfo = need.getString("queue_info");
        JSONArray array = order.getJSONArray("passengers");
        JSONArray passengers = new JSONArray();
        for (int i = 0; i < array.size(); ++i) {
            JSONObject p = array.getJSONObject(i);
            String name = p.getString("passenger_name");
            String type = p.getString("ticket_type");
            String idType = p.getString("passenger_id_name");
            String idNo = p.getString("passenger_id_no");
            Passenger passenger = new Passenger(name, type, idNo, idType, task.username, false, "");
            passengers.add(passenger.toJSON());
        }
        return new TrainAnOrder(reserveNo,
                new Timestamp(System.currentTimeMillis()), task.trainCode, task.fromStation, task.toStation,
                new Timestamp(TimeFormatUtil.date2Stamp(task.fromDate + " " + task.fromTime, "yyyyMMdd HH:mm")),
                new Timestamp(TimeFormatUtil.date2Stamp(task.toDate + " " + task.toTime, "yyyyMMdd HH:mm")),
                task.duration, seatType, queueInfo, prepayAmount, status, passengers.toJSONString(), task.userOrderId);
    }


    private void queryAndSaveTrainAnOrder(Task task) {
        TrainAnOrder trainAnOrder = queryTrainAnOrder(task);
        while (trainAnOrder == null) {
            LOGGER.error("queryAndSaveTrainAnOrder: result==null");
            priorityService.sleepRandomTime(1000,2000);
            trainAnOrder = queryTrainAnOrder(task);
        }
        trainAnOrderRepository.save(trainAnOrder);
    }

    private static class Task {
        String userOrderId;
        String username;
        String password;
        String trainCode;
        String fromStation;
        String toStation;
        String fromDate;
        String toDate;
        String fromTime;
        String toTime;
        String duration;

        Task(String userOrderId, String username, String password, String trainCode, String fromStation, String toStation, String fromDate, String toDate, String fromTime, String toTime, String duration) {
            this.userOrderId = userOrderId;
            this.username = username;
            this.password = password;
            this.trainCode = trainCode;
            this.fromStation = fromStation;
            this.toStation = toStation;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.duration = duration;
        }
    }
}
