package com.yy.statemachine;

import com.yy.constant.RushType;
import com.yy.statemachine.alternate.AlternateOrderAction;
import com.yy.statemachine.alternate.AlternateOrderContext;
import com.yy.statemachine.alternate.AlternateOrderState;
import com.yy.statemachine.alternate.states.AlternateRushingState;
import com.yy.statemachine.realtime.RealTimeOrderAction;
import com.yy.statemachine.realtime.RealTimeOrderContext;
import com.yy.dao.UserOrderRepository;
import com.yy.dao.entity.UserOrder;
import com.yy.statemachine.realtime.RealTimeOrderState;
import com.yy.statemachine.realtime.states.RealTimeRushingState;
import com.yy.statemachine.states.SleepingState;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class OrderContextManager {

    private static final Logger LOGGER = Logger.getLogger(OrderContextManager.class);

    private static final Map<String, AbstractOrderContext> orderContextMap = new ConcurrentHashMap<>();

    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private RealTimeOrderAction realTimeOrderAction;
    @Autowired
    private AlternateOrderAction alternateOrderAction;


    private void resumeRealTimeOrder() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isSleepTime = timestamp.getHours() >= 23 || timestamp.getHours() <= 5;
        List<UserOrder> orders = userOrderRepository.findAllByRushType(RushType.REAL_TIME.getType());
        for (UserOrder order : orders) {
            OrderState orderState = OrderStateManager.getOrderState(order.getStatus());
            //如果数据库中状态为休息中，但当前是抢票时间
            if (OrderState.sleepingState.equals(orderState) && !isSleepTime) {
                order.setStatus(OrderStateManager.getStateName(RealTimeOrderState.realTimeRushingState));
            }
            //如果数据库中状态为实时抢票中，但是当前是睡觉时间
            if (RealTimeOrderState.realTimeRushingState.equals(orderState) && isSleepTime) {
                order.setStatus(OrderStateManager.getStateName(OrderState.sleepingState));
            }
            //如果已过期
            if ((RealTimeOrderState.realTimeRushingState.equals(orderState) || OrderState.sleepingState.equals(orderState))
                    && System.currentTimeMillis() >= order.getExpireTime().getTime()) {
                order.setStatus(OrderStateManager.getStateName(OrderState.failedState));
            }
            add(order);
        }
    }

    private void resumeAlternateOrder() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        boolean isSleepTime = timestamp.getHours() >= 23 || timestamp.getHours() <= 5;
        List<UserOrder> orders = userOrderRepository.findAllByRushType(RushType.ALTERNATE.getType());
        for (UserOrder order : orders) {
            OrderState orderState = OrderStateManager.getOrderState(order.getStatus());
            //如果数据库中状态为休息中，但当前是抢票时间
            if (OrderState.sleepingState.equals(orderState) && !isSleepTime) {
                order.setStatus(OrderStateManager.getStateName(AlternateOrderState.alternateRushingState));
            }
            //如果数据库中但状态为实时抢票中，但是当前是睡觉时间
            if (AlternateOrderState.alternateRushingState.equals(orderState) && isSleepTime) {
                order.setStatus(OrderStateManager.getStateName(OrderState.sleepingState));
            }
            //如果已过期
            if ((AlternateOrderState.alternateRushingState.equals(orderState) || OrderState.sleepingState.equals(orderState))
                    && System.currentTimeMillis() >= order.getExpireTime().getTime()) {
                order.setStatus(OrderStateManager.getStateName(OrderState.failedState));
            }
            add(order);
        }
    }

    /**
     * 从数据库中恢复订单
     */
    @PostConstruct
    private void resume() {
        resumeRealTimeOrder();
        resumeAlternateOrder();
        LOGGER.info("从数据库中加载未完成的订单，然后去处理订单");
    }


    /**
     * 每天早上5:58执行，因为6点开售，所以可以提早一点
     */
    @Scheduled(cron = "0 58 05 ? * *")
    private void wakeup() {
        for (AbstractOrderContext context : orderContextMap.values()) {
            if (context.getState() instanceof SleepingState) {
                context.getState().wakeup(context);
            }
        }
        LOGGER.info("工作时间，启动所有处于休息中的任务");
    }

    /**
     * 每天晚上11:02点，12306休息，关闭所有抢票任务
     */
    @Scheduled(cron = "0 02 23 ? * *")
    private void sleep() {
        for (AbstractOrderContext context : orderContextMap.values()) {
            if (context.getState() instanceof RealTimeRushingState) {
                context.getState().sleep(context);
            }
            if (context.getState() instanceof AlternateRushingState) {
                context.getState().sleep(context);
            }
        }
        LOGGER.info("12306休息，关闭所有处于抢票中的任务");
    }


    /**
     * 每小时检查一次过期的订单
     */
    @Scheduled(fixedRate = 3600 * 60)
    private void checkExpire() {
        for (AbstractOrderContext context : orderContextMap.values()) {
            long currTime = System.currentTimeMillis();
            boolean expired = context.getOrder().getExpireTime().getTime() <= currTime;
            if (expired) {
                context.setState(OrderState.failedState);
            }
        }
    }


    /**
     * 添加订单
     *
     * @param order
     */
    public void add(UserOrder order) {
        //恢复之前的状态
        AbstractOrderContext orderContext = null;
        if (RushType.REAL_TIME.getType().equals(order.getRushType())) {
            orderContext = new RealTimeOrderContext(order, realTimeOrderAction);
        } else if (RushType.ALTERNATE.getType().equals(order.getRushType())) {
            orderContext = new AlternateOrderContext(order, alternateOrderAction);
        }
        if (orderContext == null) {
            return;
        }
        orderContextMap.put(order.getOrderId(), orderContext);
        OrderState orderState = OrderStateManager.getOrderState(order.getStatus());
        if (orderState == null) {
            orderContext.start();
        } else {
            orderContext.start(orderState);
        }
    }

    /**
     * 取消订单
     *
     * @param order
     */
    public void cancel(UserOrder order) {

        AbstractOrderContext orderContext = orderContextMap.get(order.getOrderId());
        if (orderContext == null) {
            return;
        }
        orderContext.getState().cancel(orderContext);
    }

}
