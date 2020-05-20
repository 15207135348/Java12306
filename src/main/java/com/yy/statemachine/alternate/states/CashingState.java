package com.yy.statemachine.alternate.states;


import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.alternate.AlternateOrderAction;
import com.yy.statemachine.alternate.AlternateOrderState;
import com.yy.statemachine.states.CanceledState;
import com.yy.statemachine.states.SleepingState;

public class CashingState implements AlternateOrderState {


    @Override
    public void entry(AbstractOrderContext context) {

        if (!context.isRunning()) {
            return;
        }

        //更新订单状态
        AlternateOrderAction action = (AlternateOrderAction) context.getAction();
        action.update(context);
        //监测是否兑现成功
        if (action.checkCash(context)) {
            //保存已支付的订单
            action.saveCashedOrder(context);
            cashSuccess(context);
        } else {
            if (!(context.getState() instanceof SleepingState) && !(context.getState() instanceof CanceledState)) {
                cashFailed(context);
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

        //先向12306请求，取消未兑现订单
        AlternateOrderAction action = (AlternateOrderAction) context.getAction();
        action.cancelUncashedOrder(context);
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

    }

    @Override
    public void payFailed(AbstractOrderContext context) {

    }

    @Override
    public void cashSuccess(AbstractOrderContext context) {
        context.setState(finishedState);
    }

    @Override
    public void cashFailed(AbstractOrderContext context) {
        context.setState(failedState);
    }
}
