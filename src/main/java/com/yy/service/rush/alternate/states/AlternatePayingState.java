package com.yy.service.rush.alternate.states;


import com.yy.service.rush.AbstractOrderContext;
import com.yy.service.rush.alternate.AlternateOrderAction;
import com.yy.service.rush.alternate.AlternateOrderState;
import com.yy.service.rush.states.CanceledState;
import com.yy.service.rush.states.SleepingState;

public class AlternatePayingState implements AlternateOrderState {


    @Override
    public void entry(AbstractOrderContext context) {

        if (!context.isRunning()){
            return;
        }

        AlternateOrderAction action = (AlternateOrderAction) context.getAction();
        //更新订单状态
        action.update(context);
        //保存未支付订单
        action.saveUnpaidAlternateOrder(context);
        //通知用户付款
        action.notifyAlternatePay(context);
        //实时监测用户是否支付
        boolean success = action.checkAlternatePay(context);
        if (success){
            paySuccess(context);
        }else {
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
        AlternateOrderAction action = (AlternateOrderAction) context.getAction();
        action.cancelUnpaidAlternateOrder(context);

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
    public void submitFailed(AbstractOrderContext context) {

    }

    @Override
    public void submitSuccess(AbstractOrderContext context) {

    }

    @Override
    public void paySuccess(AbstractOrderContext context) {
        context.setState(cashingState);
    }

    @Override
    public void payFailed(AbstractOrderContext context) {
        context.setState(failedState);
    }

    @Override
    public void cashSuccess(AbstractOrderContext context) {

    }

    @Override
    public void cashFailed(AbstractOrderContext context) {

    }
}
