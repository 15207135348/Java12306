package com.yy.statemachine.alternate;


import com.yy.api.rail.OrderCenter;
import com.yy.api.rail.OrderSubmitter;
import com.yy.dao.*;
import com.yy.dao.entity.TrainAnOrder;
import com.yy.dao.entity.WxAccount;
import com.yy.exception.UnfinishedOrderException;
import com.yy.observer.FindResult;
import com.yy.observer.Finder;
import com.yy.service.SendMsgService;
import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.OrderAction;
import com.yy.statemachine.OrderState;
import com.yy.statemachine.alternate.states.AlternatePayingState;
import com.yy.util.SleepUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AlternateOrderAction extends OrderAction {

    @Autowired
    private SendMsgService sendMsgService;
    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private WxAccountRepository wxAccountRepository;
    @Autowired
    private TrainAnOrderRepository trainAnOrderRepository;

    /**
     * 数据库更新订单状态
     *
     * @param context
     */
    public boolean update(AbstractOrderContext context) {
        OrderState state = context.getState();
        if (!(state instanceof AbstractAlternateOrderState)){
            return super.update(context);
        }
        context.getOrder().setStatus(((AbstractAlternateOrderState) state).getStateName());
        userOrderRepository.save(context.getOrder());
        return true;
    }

    public boolean findAlternate(AbstractOrderContext context) {
        return Finder.findAlternate(context).isFound();
    }

    /**
     * 提交候补订单
     *
     * @param context
     * @return
     */
    public boolean submitAlternate(AbstractOrderContext context) throws UnfinishedOrderException {
        FindResult findResult = context.getFindResult();
        if (findResult == null) {
            return false;
        }
        WxAccount wxAccount = wxAccountRepository.findByOpenId(context.getOrder().getOpenId());
        return OrderSubmitter.submitAfterNate(
                wxAccount.getUsername(),
                wxAccount.getPassword(),
                findResult.getTrain().getSecretStr(),
                findResult.getSeats(),
                Arrays.asList(context.getOrder().getPeople().split("/"))
        );
    }

    public boolean saveUnpaidAlternateOrder(AbstractOrderContext context) {
        WxAccount wxAccount = wxAccountRepository.findByOpenId(context.getOrder().getOpenId());

        FindResult findResult = context.getFindResult();
        if (findResult == null) {
            return false;
        }
        TrainAnOrder trainAnOrder = OrderCenter.findUnpaidAnOrder(wxAccount.getUsername(), wxAccount.getPassword(),
                findResult.getTrain(), context.getOrder().getOrderId());

        while (trainAnOrder == null) {
            SleepUtil.sleepRandomTime(1000, 2000);
            trainAnOrder = OrderCenter.findUnpaidAnOrder(wxAccount.getUsername(), wxAccount.getPassword(),
                    findResult.getTrain(), context.getOrder().getOrderId());
        }

        trainAnOrderRepository.save(trainAnOrder);

        return true;
    }


    public boolean notifyAlternatePay(AbstractOrderContext context) {
        TrainAnOrder trainAnOrder = trainAnOrderRepository.findByUserOrderId(context.getOrder().getOrderId());
        if (trainAnOrder == null) {
            return false;
        }
        if (!(context.getState() instanceof AlternatePayingState)) {
            return false;
        }
        sendMsgService.sendAlternate(context.getOrder());
        return true;
    }

    public boolean checkAlternatePay(AbstractOrderContext context) {
        return false;
    }

    public boolean cancelUnpaidAlternateOrder(AbstractOrderContext context) {
        return false;
    }

    public boolean checkCash(AbstractOrderContext context) {
        return false;
    }

    public boolean cancelUncashedOrder(AbstractOrderContext context) {
        return false;
    }

}


