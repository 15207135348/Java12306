package com.yy.service.core;

import com.yy.constant.UserOrderStatus;
import com.yy.dao.UserOrderRepository;
import com.yy.dao.entity.UserOrder;
import com.yy.domain.Session;
import com.yy.domain.Train;
import com.yy.service.util.PriorityService;
import com.yy.service.util.SendMsgService;
import com.yy.service.util.SessionPoolService;
import com.yy.service.util.StationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 该类专门负责查询12306的车票信息
 */
@Service
@EnableScheduling
public class UserOrderPoolService {

    private static final Logger LOGGER = Logger.getLogger(UserOrderPoolService.class);
    //查票任务
    private Map<String, QueryHandler> handlerMap = new ConcurrentHashMap<>();
    private ThreadPoolExecutor threads = new ThreadPoolExecutor(10,
        20, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));
    @Autowired
    private StationService stationService;
    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private QueryTrainService queryTrainTicketsService;
    @Autowired
    private SessionPoolService sessionPoolService;
    @Autowired
    private SubmitOrderService submitOrderService;
    @Autowired
    private SendMsgService sendMsgService;
    @Autowired
    private MonitorTrainOrderService monitorTrainOrderService;
    @Autowired
    private MonitorTrainAnOrderService monitorTrainAnOrderService;
    @Autowired
    private PriorityService priorityService;

    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());


    public boolean isInBreakTime() {
        timestamp.setTime(System.currentTimeMillis());
        return timestamp.getHours() >= 23 || timestamp.getHours() <= 5;
    }

    @Scheduled(fixedRate = 5000)
    private void print() {
        int total = 0;
        for (String name : handlerMap.keySet()) {
            int count = handlerMap.get(name).getTaskCount();
            LOGGER.info(String.format("QueryHandler（%s）有【%d】个任务", name, count));
            total += count;
        }
        LOGGER.info(String.format("总共有【%d】个任务", total));
    }

    //从数据库中加载未完成的订单，然后去处理订单
    //启动项目时执行 以及 每天早上5:58执行，因为6点开售，所以可以提早一点
    @PostConstruct
    private void loadOrder() {
        List<UserOrder> orders = userOrderRepository.findAll();
        for (UserOrder order : orders) {
            if (order.getStatus().equals(UserOrderStatus.RUSHING.getStatus())) {
                addOrder(order);
            }
            if (order.getStatus().equals(UserOrderStatus.AN_RUSHING.getStatus())) {
                addOrder(order);
            }
        }
        LOGGER.info("从数据库中加载未完成的订单，然后去处理订单");
    }

    private String genQueryID(String date, String fromStation, String toStation, int priority) {
        return String.format("%s-%s-%s-%d", date, fromStation, toStation, priority);
    }

    private void removeOrderFromQueue(UserOrder order) {
        InnerOrder innerOrder = new InnerOrder(order);
        String name;
        for (String date : innerOrder.dates) {
            name = genQueryID(date, order.getFromStation(), order.getToStation(), order.getPriority());
            if (handlerMap.containsKey(name)) {
                QueryHandler handler = handlerMap.get(name);
                handler.delTask(innerOrder);
            }
        }
    }

    public void addOrder(UserOrder order) {
        InnerOrder innerOrder = new InnerOrder(order);
        String name;
        for (String date : innerOrder.dates) {
            name = genQueryID(date, order.getFromStation(), order.getToStation(), order.getPriority());
            QueryHandler handler;
            if (handlerMap.containsKey(name)) {
                handler = handlerMap.get(name);
                handler.addTask(innerOrder);
            } else {
                handler = new QueryHandler(name, date, order.getFromStation(), order.getToStation(), order.getPriority());
                handlerMap.put(name, handler);
                handler.addTask(innerOrder);
                threads.execute(handler);
            }
        }
    }

    public void cancelOrder(UserOrder order) {
        removeOrderFromQueue(order);
        order.setStatus(UserOrderStatus.CANCELED.getStatus());
        userOrderRepository.save(order);
    }

    private static class MatchResult {
        boolean success = false;
        //匹配到的车票
        Train train;
        //匹配到的订单
        InnerOrder innerOrder;
        //日期
        String date;
        //需要找到一个车次，有people.size()个座位，这些座位最好是同一类型，但也可以不同
        List<String> foundSeats = new ArrayList<>();
    }

    private static class InnerOrder {
        //是否正在提交订单
        AtomicBoolean submitting;
        UserOrder order;
        boolean isAlternate;
        Set<String> trainCodes = new HashSet<>();
        Set<String> dates = new HashSet<>();
        Set<String> seats = new HashSet<>();
        List<String> people = new ArrayList<>();

        InnerOrder(UserOrder userOrder) {
            this.submitting = new AtomicBoolean(false);
            this.order = userOrder;
            this.isAlternate = userOrder.getRushType().equals("候补抢票");
            Collections.addAll(trainCodes, userOrder.getTrains().split("/"));
            Collections.addAll(seats, userOrder.getSeats().split("/"));
            Collections.addAll(dates, userOrder.getDates().split("/"));
            Collections.addAll(people, userOrder.getPeople().split("/"));
        }
    }

    private class QueryHandler implements Runnable {

        private final Logger LOGGER = Logger.getLogger(QueryHandler.class);
        private Session session;
        private String name;
        private String date;
        private String fromStation;
        private String toStation;
        private int priority;

        private Map<String, InnerOrder> tasks = new ConcurrentHashMap<>();

        QueryHandler(String name, String date, String fromStation, String toStation, int priority) {
            this.name = name;
            this.session = sessionPoolService.getSession(null);
            this.date = date;
            this.fromStation = fromStation;
            this.toStation = toStation;
            this.priority = priority;
        }

        void addTask(InnerOrder innerOrder) {
            tasks.put(innerOrder.order.getOrderId(), innerOrder);
        }

        void delTask(InnerOrder innerOrder) {
            tasks.remove(innerOrder.order.getOrderId());
        }

        int getTaskCount() {
            return tasks.size();
        }

        @Override
        public void run() {
            while (!tasks.isEmpty()) {
                try {
                    if (isInBreakTime()) {
                        Thread.sleep(60000);
                        LOGGER.info("12306休息时间");
                        continue;
                    }
                    //查询一次火车票
                    List<Train> trainTickets = queryTrainTicketsService.getTrains(
                        session, date, fromStation, toStation);
                    //更新查询次数
                    for (InnerOrder innerOrder : tasks.values()) {
                        userOrderRepository.addAndGetQueryCountByOrderId(innerOrder.order.getOrderId(), 1);
                    }
                    if (trainTickets == null) {
                        sessionPoolService.remove(null);
                        session = sessionPoolService.getSession(null);
                        continue;
                    }
                    //匹配符合要求的火车票
                    InnerOrder innerOrder;
                    for (String key : tasks.keySet()) {
                        innerOrder = tasks.get(key);
                        //如果订单过期
                        if (isExpired(innerOrder)) {
                            LOGGER.info(String.format("订单【%s】已过期", key));
                            innerOrder.order.setStatus(UserOrderStatus.EXPIRED.getStatus());
                            userOrderRepository.save(innerOrder.order);
                            tasks.remove(key);
                            continue;
                        }
                        //如果已经抢票成功，则移除
                        if (innerOrder.order.getStatus().equals(UserOrderStatus.SUCCESS.getStatus())) {
                            LOGGER.info(String.format("订单【%s】状态:%s，删除订单", key, innerOrder.order.getStatus()));
                            tasks.remove(key);
                            continue;
                        }
                        //如果已经候补抢票成功，则移除
                        if (innerOrder.order.getStatus().equals(UserOrderStatus.AN_SUCCESS.getStatus())) {
                            LOGGER.info(String.format("订单【%s】状态:%s，删除订单", key, innerOrder.order.getStatus()));
                            tasks.remove(key);
                            continue;
                        }
                        //匹配订单
                        MatchResult matchResult = matchOrder(trainTickets, tasks.get(key));
                        if (matchResult.success && submit(matchResult)) {
                            tasks.remove(key);
                        }
                    }
                    priorityService.sleep(priority);
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
            handlerMap.remove(name);
        }

        //定期检查订单是否过期
        private boolean isExpired(InnerOrder innerOrder) {
            long currTime = System.currentTimeMillis();
            return innerOrder.order.getExpireTime().getTime() <= currTime;
        }

        /**
         * 订单的 起始站 终点站 出发日期已经是满足的了，
         * 这里需要匹配：是否有订单需要的车次和坐席信息。
         * 如果找到，则返回该车次信息，同时foundSeats应该包含people.size()个座位
         *
         * @param trainList
         * @param innerOrder
         * @return
         */
        private MatchResult matchOrder(List<Train> trainList, InnerOrder innerOrder) {
            MatchResult matchResult = new MatchResult();
            //打乱匹配的顺序，防止因为某辆列车候补人数较多导致每次都下单失败
            Collections.shuffle(trainList);
            for (Train train : trainList) {
                //检查是否是选择的车次，不是就直接过滤
                if (!innerOrder.trainCodes.contains(train.getTrainCode())) {
                    continue;
                }
                //用户不愿意坐多辆车，所以在匹配每趟车时，需要清空foundSeats
                matchResult.foundSeats.clear();
                //如果属于实时抢票订单，则匹配有票的座位
                if (!innerOrder.isAlternate) {
                    //检查有没有同一种座位类型的车票
                    for (String seat : innerOrder.seats) {
                        String count = train.getTicketCount(seat);
                        //如果某一种座位有足够的票，则说明找到了
                        if ((count.equals("有") || (count.matches("^[1-9]\\d*$")
                            && Integer.parseInt(count) >= innerOrder.people.size()))) {
                            LOGGER.info(String.format("【%s】座位类型有足够的票", seat));
                            //防止添加了其它座位类型
                            matchResult.foundSeats.clear();
                            for (int i = 0; i < innerOrder.people.size(); ++i) {
                                matchResult.foundSeats.add(seat);
                            }
                            matchResult.date = date;
                            matchResult.success = true;
                            matchResult.train = train;
                            matchResult.innerOrder = innerOrder;
                            return matchResult;
                        }
                    }
                    //检查有没有不同类型座位的车票
                    for (String seat : innerOrder.seats) {
                        String count = train.getTicketCount(seat);
                        //如果某种座位有票但不足够
                        if (count.matches("^[1-9]\\d*$")
                            && Integer.parseInt(count) < innerOrder.people.size()) {
                            LOGGER.info(String.format("【%s】座位类型有【%s】张票，已添加进座位列表", seat, count));
                            for (int i = 0; i < Integer.parseInt(count); ++i) {
                                if (matchResult.foundSeats.size() == innerOrder.people.size()) {
                                    LOGGER.info("多种类型的座位混合，有了足够的票");
                                    matchResult.success = true;
                                    matchResult.train = train;
                                    matchResult.innerOrder = innerOrder;
                                    return matchResult;
                                }
                                matchResult.foundSeats.add(seat);
                            }
                        }
                    }
                }
                //如果是候补抢票订单，则匹配没有票但是可以候补的座位
                else {
                    //检查有没有候补车票
                    for (String seat : innerOrder.seats) {
                        String count = train.getTicketCount(seat);
                        //如果是候补订单，且该车次到座位类型支持候补抢票
                        if (count.equals("无") && train.isCanBackup()) {
                            LOGGER.info(String.format("【%s】车次的【%s】座位类型支持候补抢票", train.getTrainCode(), seat));
                            //防止添加了其它座位类型
                            matchResult.foundSeats.clear();
                            for (int i = 0; i < innerOrder.people.size(); ++i) {
                                matchResult.foundSeats.add(seat);
                            }
                            matchResult.success = true;
                            matchResult.train = train;
                            matchResult.innerOrder = innerOrder;
                            return matchResult;
                        }
                    }
                }
            }
            //所有车次都不能找到足够的座位，则匹配失败
            return matchResult;
        }

        private boolean submit(MatchResult matchResult) {
            //如果订单没有任何一个线程在提交，则该线程提交订单
            if (!matchResult.innerOrder.submitting.get()) {
                //设置当前为正在提交状态
                matchResult.innerOrder.submitting.set(true);
                boolean success = false;
                try {
                    LOGGER.info(String.format("submit:正在提交订单【%s】", matchResult.innerOrder.order.getOrderId()));
                    success = submitCommon(matchResult);
                    if (success) {
                        monitorTrainOrderService.addTask(matchResult.innerOrder.order, matchResult.train);
                        LOGGER.info(String.format("submit:订单【%s】提交成功", matchResult.innerOrder.order.getOrderId()));
                    }
                    success = submitAfterNate(matchResult);
                    if (success) {
                        monitorTrainAnOrderService.addTask(matchResult.innerOrder.order, matchResult.train);
                        LOGGER.info(String.format("submit:候补订单【%s】提交成功", matchResult.innerOrder.order.getOrderId()));
                    }
                }catch (Exception e){
                    LOGGER.error("submit:" + e.getMessage());
                }
                //设置不在提交状态
                matchResult.innerOrder.submitting.set(false);
                return success;
            }
            LOGGER.info(String.format("submit:订单【%s】正处于提交状态，该线程不提交", matchResult.innerOrder.order.getOrderId()));
            return false;
        }

        private boolean submitCommon(MatchResult matchResult) {
            if (matchResult.innerOrder.isAlternate) {
                return false;
            }
            String sequenceNo = submitOrderService.submit(
                matchResult.innerOrder.order.getOpenId(),
                matchResult.train.getSecretStr(),
                matchResult.date,
                matchResult.innerOrder.order.getFromStation(),
                matchResult.innerOrder.order.getToStation(),
                matchResult.innerOrder.people,
                new ArrayList<>(matchResult.foundSeats));
            //下单失败
            if (sequenceNo == null) {
                LOGGER.warn(String.format("订单【%s】下单失败！", matchResult.innerOrder.order.getOrderId()));
                return false;
            }
            //下单成功
            LOGGER.info(String.format("订单【%s】下单成功，订单号为:%s", matchResult.innerOrder.order.getOrderId(), sequenceNo));
            matchResult.innerOrder.order.setStatus(UserOrderStatus.SUCCESS.getStatus());
            userOrderRepository.save(matchResult.innerOrder.order);
            sendMsgService.send(matchResult.innerOrder.order, sequenceNo);
            return true;
        }

        private boolean submitAfterNate(MatchResult matchResult) {
            if (!matchResult.innerOrder.isAlternate) {
                return false;
            }
            boolean success = submitOrderService.submitByAfterNate(
                matchResult.innerOrder.order.getOpenId(),
                matchResult.train.getSecretStr(),
                matchResult.foundSeats,
                matchResult.innerOrder.people);
            //候补下单失败
            if (!success) {
                LOGGER.warn(String.format("订单【%s】候补下单失败！", matchResult.innerOrder.order.getOrderId()));
                return false;
            }
            //候补下单成功
            LOGGER.info(String.format("订单【%s】候补下单成功", matchResult.innerOrder.order.getOrderId()));
            matchResult.innerOrder.order.setStatus(UserOrderStatus.AN_SUCCESS.getStatus());
            userOrderRepository.save(matchResult.innerOrder.order);
            sendMsgService.sendAlternate(matchResult.innerOrder.order);
            return true;
        }

    }
}
