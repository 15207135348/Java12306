package com.yy.statemachine.realtime;


import com.yy.api.rail.OrderCenter;
import com.yy.api.rail.OrderSubmitter;
import com.yy.dao.*;
import com.yy.dao.entity.TrainOrder;
import com.yy.dao.entity.WxAccount;
import com.yy.exception.UnfinishedOrderException;
import com.yy.domain.FindResult;
import com.yy.observer.Finder;
import com.yy.service.SendMsgService;
import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.OrderAction;
import com.yy.statemachine.realtime.states.RealTimePayingState;
import com.yy.util.SleepUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RealTimeOrderAction extends OrderAction {

    @Autowired
    private SendMsgService sendMsgService;
    @Autowired
    private TrainTicketRepository trainTicketRepository;
    @Autowired
    private TrainOrderRepository trainOrderRepository;

    /**
     * 查询是否有符合要求的车票
     *
     * @param context
     * @return
     */
    public boolean findRealTime(AbstractOrderContext context) {
        return Finder.findRealTime(context).isFound();
    }

    /**
     * 提交订单
     *
     * @param context
     * @return
     */
    public boolean submitRealTime(AbstractOrderContext context) throws UnfinishedOrderException {
        FindResult findResult = context.getFindResult();
        if (findResult == null) {
            return false;
        }
        WxAccount wxAccount = context.getWxAccount();
        String sequenceNo = OrderSubmitter.submitRealTime(
                wxAccount.getUsername(),
                wxAccount.getPassword(),
                findResult.getTrain().getSecretStr(),
                findResult.getDate(),
                context.getOrder().getFromStation(),
                context.getOrder().getToStation(),
                Arrays.asList(context.getOrder().getPeople().split("/")),
                findResult.getSeats()
        );
        return sequenceNo != null;
    }

    /**
     * 保存已抢到到车票
     *
     * @param context
     * @return
     */
    public boolean saveUnpaidRealTimeOrder(AbstractOrderContext context) {

        WxAccount wxAccount = context.getWxAccount();

        FindResult findResult = context.getFindResult();
        if (findResult == null) {
            return false;
        }
        OrderCenter.Result result = OrderCenter.findUnpaidOrder(wxAccount.getUsername(), wxAccount.getPassword(),
                findResult.getTrain(), context.getOrder().getOrderId());

        while (result == null) {
            SleepUtil.sleepRandomTime(1000, 2000);
            result = OrderCenter.findUnpaidOrder(wxAccount.getUsername(), wxAccount.getPassword(),
                    findResult.getTrain(), context.getOrder().getOrderId());
        }
        trainOrderRepository.save(result.getTrainOrder());
        trainTicketRepository.save(result.getTickets());
        return true;
    }

    /**
     * 通知用户付款
     *
     * @param context
     */
    public boolean notifyRealTimePay(AbstractOrderContext context) {
        TrainOrder trainOrder = trainOrderRepository.findByUserOrderId(context.getOrder().getOrderId());
        if (trainOrder == null) {
            return false;
        }
        if (!(context.getState() instanceof RealTimePayingState)) {
            return false;
        }
        sendMsgService.send(context.getOrder(), trainOrder.getSequenceNo());
        return true;
    }


    /**
     * 检查用户是否支付
     *
     * @param context
     * @return 支付成功或者失败
     */
    public boolean checkRealTimePay(AbstractOrderContext context) {
        WxAccount wxAccount = context.getWxAccount();
        String orderId = context.getOrder().getOrderId();
        TrainOrder trainOrder = trainOrderRepository.findByUserOrderId(orderId);
        boolean isUntraveledOrder = false;
        long expiredTime = System.currentTimeMillis() + 30 * 60000;
        while (context.isRunning() && System.currentTimeMillis() < expiredTime) {
            isUntraveledOrder = OrderCenter.isUntraveledOrder(wxAccount.getUsername(), wxAccount.getPassword(), trainOrder.getSequenceNo());
            if (isUntraveledOrder) {
                break;
            }
            //30秒钟检测一次
            SleepUtil.sleep(30000);
        }
        return isUntraveledOrder;
    }

    public boolean cancelUnpaidRealTimeOrder(AbstractOrderContext context) {
        WxAccount wxAccount = context.getWxAccount();
        String orderId = context.getOrder().getOrderId();
        TrainOrder trainOrder = trainOrderRepository.findByUserOrderId(orderId);
        return OrderCenter.cancelNoCompleteOrder(wxAccount.getUsername(), wxAccount.getPassword(), trainOrder.getSequenceNo());
    }

}


