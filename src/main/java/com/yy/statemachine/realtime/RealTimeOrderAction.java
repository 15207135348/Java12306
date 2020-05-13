package com.yy.statemachine.realtime;


import com.yy.api.rail.OrderCenter;
import com.yy.api.rail.OrderSubmitter;
import com.yy.dao.*;
import com.yy.dao.entity.TrainOrder;
import com.yy.dao.entity.WxAccount;
import com.yy.exception.UnfinishedOrderException;
import com.yy.observer.FindResult;
import com.yy.observer.Finder;
import com.yy.service.SendMsgService;
import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.OrderAction;
import com.yy.statemachine.OrderState;
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
    private UserOrderRepository userOrderRepository;
    @Autowired
    private WxAccountRepository wxAccountRepository;
    @Autowired
    private TrainTicketRepository trainTicketRepository;
    @Autowired
    private TrainOrderRepository trainOrderRepository;

    /**
     * 数据库更新订单状态
     *
     * @param context
     */
    public boolean update(AbstractOrderContext context) {
        OrderState state = context.getState();
        if (!(state instanceof AbstractRealTimeOrderState)){
            return super.update(context);
        }
        context.getOrder().setStatus(((AbstractRealTimeOrderState) state).getStateName());
        userOrderRepository.save(context.getOrder());
        return true;
    }

    /**
     * 查询是否有符合要求的车票
     *
     * @param context
     * @return
     */
    public boolean findRealTime(AbstractOrderContext context) {
        Finder.findRealTime(context);
        return true;
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
        WxAccount wxAccount = wxAccountRepository.findByOpenId(context.getOrder().getOpenId());
        //提交实时订单
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

        WxAccount wxAccount = wxAccountRepository.findByOpenId(context.getOrder().getOpenId());

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
        return true;
    }

    public boolean cancelUnpaidRealTimeOrder(AbstractOrderContext context) {
        return false;
    }

}


