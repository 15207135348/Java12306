package com.yy.service.rush.alternate.states;


import com.yy.service.rush.AbstractOrderContext;
import com.yy.service.rush.alternate.AlternateOrderAction;
import com.yy.service.rush.alternate.AlternateOrderState;


public class AlternateRushingState implements AlternateOrderState {


    @Override
    public void entry(AbstractOrderContext context) {

        if (!context.isRunning()){
            return;
        }

        //更新订单状态
        AlternateOrderAction action = (AlternateOrderAction) context.getAction();
        action.update(context);
        //阻塞，直到找到候补车票
        if(action.findAlternate(context)){
            //触发found事件
            found(context);
        }
    }

    @Override
    public void exit(AbstractOrderContext context) {

    }

    @Override
    public void sleep(AbstractOrderContext context) {
        context.setState(sleepingState);
    }

    @Override
    public void wakeup(AbstractOrderContext context) {

    }

    @Override
    public void cancel(AbstractOrderContext context) {
        context.setState(canceledState);
    }

    @Override
    public void expire(AbstractOrderContext context) {
        context.setState(failedState);
    }

    @Override
    public void conflict(AbstractOrderContext context) {

    }

    @Override
    public void found(AbstractOrderContext context) {
        context.setState(alternateSubmittingState);
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

    }

    @Override
    public void cashFailed(AbstractOrderContext context) {

    }


}
