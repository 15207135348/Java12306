package com.yy.statemachine.realtime.states;

import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.realtime.RealTimeOrderAction;
import com.yy.statemachine.realtime.RealTimeOrderState;
import com.yy.statemachine.states.CanceledState;
import com.yy.statemachine.states.SleepingState;

public class RealTimePayingState implements RealTimeOrderState {


    @Override
    public void entry(AbstractOrderContext context) {

        if (!context.isRunning()) {
            return;
        }

        RealTimeOrderAction action = (RealTimeOrderAction) context.getAction();
        //更新订单状态
        action.update(context);
        //保存未支付订单
        action.saveUnpaidRealTimeOrder(context);
        //通知用户付款
        action.notifyRealTimePay(context);
        //实时监测用户是否支付
        boolean success = action.checkRealTimePay(context);
        if (success) {
            paySuccess(context);
        } else {
            if (!(context.getState() instanceof SleepingState) && !(context.getState() instanceof CanceledState)){
                payFailed(context);
            }
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
        RealTimeOrderAction action = (RealTimeOrderAction) context.getAction();
        action.cancelUnpaidRealTimeOrder(context);

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
