package com.yy.statemachine.alternate.states;


import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.alternate.AbstractAlternateOrderState;
import com.yy.statemachine.alternate.AlternateOrderAction;
import com.yy.statemachine.alternate.AlternateOrderState;


public class AlternateRushingState extends AbstractAlternateOrderState {

    public AlternateRushingState(String stateName) {
        super(stateName);
    }

    @Override
    public void entry(AbstractOrderContext context) {
        //更新订单状态
        AlternateOrderAction action = (AlternateOrderAction) context.getAction();
        action.update(context);
        //阻塞，直到找到候补车票
        action.findAlternate(context);
        //触发found事件
        found(context);
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
