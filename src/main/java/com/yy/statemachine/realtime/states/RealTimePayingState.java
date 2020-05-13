package com.yy.statemachine.realtime.states;

import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.realtime.AbstractRealTimeOrderState;
import com.yy.statemachine.realtime.RealTimeOrderAction;
import com.yy.statemachine.realtime.RealTimeOrderState;

public class RealTimePayingState extends AbstractRealTimeOrderState {

    public RealTimePayingState(String orderName) {
        super(orderName);
    }

    @Override
    public void entry(AbstractOrderContext context) {
        RealTimeOrderAction action = (RealTimeOrderAction) context.getAction();
        //更新订单状态
        action.update(context);
        //保存未支付订单
        action.saveUnpaidRealTimeOrder(context);
        //通知用户付款
        action.notifyRealTimePay(context);
        //实时监测用户是否支付
        boolean success = action.checkRealTimePay(context);
        if (success){
            paySuccess(context);
        }else {
            payFailed(context);
        }
    }

    @Override
    public void exit(AbstractOrderContext context) {

    }
    
    @Override
    public void sleep(AbstractOrderContext context) {

    }

    @Override
    public void wakeup(AbstractOrderContext context) {

    }

    @Override
    public void cancel(AbstractOrderContext context) {
        //先向12306请求，取消未支付订单

        //再讲订单状态设置未已取消
        context.setState(canceledState);
    }

    @Override
    public void expire(AbstractOrderContext context) {

    }

    @Override
    public void conflict(AbstractOrderContext context) {
    }

    @Override
    public void found(AbstractOrderContext context) {

    }

    @Override
    public void submitSuccess(AbstractOrderContext context) {

    }

    @Override
    public void submitFailed(AbstractOrderContext context) {

    }

    @Override
    public void paySuccess(AbstractOrderContext context) {
        context.setState(finishedState);
    }

    @Override
    public void payFailed(AbstractOrderContext context) {
        context.setState(failedState);
    }
}
