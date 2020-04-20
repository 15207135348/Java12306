package com.yy.service.core;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yy.constant.TrainOrderStatus;
import com.yy.dao.TrainOrderRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 查询我的订单（未完成订单、未出行订单、历史订单）
 */
@Service
@EnableScheduling
public class MonitorTrainOrderService {

    private static final Logger LOGGER = Logger.getLogger(MonitorTrainOrderService.class);
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
    TrainOrderRepository trainOrderRepository;
    @Autowired
    PriorityService priorityService;
    //用户订单->抢到单的时间
    private Map<Task, Long> tasks = new ConcurrentHashMap<>();

    void addTask(UserOrder order, Train train) {
        WxAccount wxAccount = wxAccountRepository.findByOpenId( order.getOpenId());
        Task task = new Task(order.getOrderId(),
            wxAccount.getUsername(), wxAccount.getPassword(),
            train.getTrainCode(), train.getFromStation(), train.getToStation(),
            train.getFromDate(), train.getToDate(), train.getFromTime(),
            train.getToTime(), train.getDuration());
        //保存订单
        queryAndSaveTrainOrder(task);
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
                trainOrderRepository.updateStatusByUserOrderId(
                    TrainOrderStatus.PAY_TIMEOUT.getStatus(),
                    task.userOrderId);
                tasks.remove(task);
                //否则，说明仍然未支付
                LOGGER.info(String.format("用户订单【%s】支付超时", task.userOrderId));
                continue;
            }
            //如果未超时，查看是否支付
            Result result = queryTrainOrder(task);
            if (result == null) {
                //找不到未完成订单，说明已经支付了
                trainOrderRepository.updateStatusByUserOrderId(
                    TrainOrderStatus.COMPLETED.getStatus(),
                    task.userOrderId);
                tasks.remove(task);
                //否则，说明仍然未支付
                LOGGER.info(String.format("用户订单【%s】已支付", task.userOrderId));
            } else {
                //否则，说明仍然未支付
                LOGGER.info(String.format("用户订单【%s】还未支付", task.userOrderId));
            }
        }
    }

    private Result queryTrainOrder(Task task) {
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
        JSONObject jsonObject = api12306Service.queryNoCompleteOrder(session);
        if (!jsonObject.containsKey("data")) {
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        if (!jsonObject.containsKey("orderDBList")) {
            return null;
        }
        JSONArray orderDBList = jsonObject.getJSONArray("orderDBList");
        if (orderDBList.isEmpty()) {
            return null;
        }
        JSONArray array = orderDBList.getJSONObject(0).getJSONArray("tickets");
        if (array == null) {
            return null;
        }
        TrainOrder trainOrder = null;
        List<TrainTicket> tickets = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); ++i) {
            JSONObject obj = array.getJSONObject(i);
            if (i == 0) {
                String sequenceNo = obj.getString("sequence_no");
                long orderTime = TimeFormatUtil.dateToStamp(obj.getString("reserve_time"));
                String ticketStatus = obj.getString("ticket_status_name");
                trainOrder = new TrainOrder(sequenceNo, new Timestamp(orderTime),
                    task.trainCode, task.fromStation, task.toStation,
                    new Timestamp(TimeFormatUtil.date2Stamp(task.fromDate + " " +task.fromTime, "yyyyMMdd HH:mm")),
                    new Timestamp(TimeFormatUtil.date2Stamp(task.toDate + " " + task.toTime, "yyyyMMdd HH:mm")),
                    task.duration, ticketStatus, task.userOrderId);
            }
            //乘客
            JSONObject passengerDTO = obj.getJSONObject("passengerDTO");
            String passengerName = passengerDTO.getString("passenger_name");
            String passengerIdType = passengerDTO.getString("passenger_id_type_name");
            String passengerId = passengerDTO.getString("passenger_id_no");
            //票的信息
            String coachName = obj.getString("coach_name");
            String seatName = obj.getString("seat_name");
            String seatType = obj.getString("seat_type_name");
            String ticketType = obj.getString("ticket_type_name");
            String price = obj.getString("str_ticket_price_page");
            String ticketNo = obj.getString("ticket_no");

            TrainTicket trainTicket = new TrainTicket(ticketNo, ticketType, passengerName, passengerId,
                passengerIdType, coachName, seatName, seatType, price, task.userOrderId);
            tickets.add(trainTicket);
        }
        Result result = new Result();
        result.trainOrder = trainOrder;
        result.tickets = tickets;
        return result;
    }


    private void queryAndSaveTrainOrder(Task task) {
        Result result = queryTrainOrder(task);
        while (result == null) {
            LOGGER.error("queryAndSaveTrainOrder: result==null");
            priorityService.sleepRandomTime(1000,2000);
            result = queryTrainOrder(task);
        }
        LOGGER.info("queryAndSaveTrainOrder: " + result.tickets);
        trainOrderRepository.save(result.trainOrder);
        trainTicketRepository.save(result.tickets);
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

        public Task(String userOrderId, String username, String password, String trainCode, String fromStation, String toStation, String fromDate, String toDate, String fromTime, String toTime, String duration) {
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

    private static class Result {
        TrainOrder trainOrder;
        List<TrainTicket> tickets;
    }
}
