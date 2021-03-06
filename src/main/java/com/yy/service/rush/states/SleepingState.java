package com.yy.service.rush.states;

import com.yy.service.rush.AbstractOrderContext;
import com.yy.service.rush.OrderAction;
import com.yy.service.rush.OrderState;


public class SleepingState implements OrderState {

    @Override
    public void entry(AbstractOrderContext context) {

        OrderAction action = context.getAction();
        action.update(context);
        context.stop();
    }

    @Override
    public void exit(AbstractOrderContext context) {

    }

    @Override
    public void sleep(AbstractOrderContext context) {

    }

    @Override
    public void wakeup(AbstractOrderContext context) {
        context.start();
    }

    @Override
    public void cancel(AbstractOrderContext context) {
        context.setState(canceledState);
    }

    @Override
    public void expire(AbstractOrderContext context) {

    }

    @Override
    public void found(AbstractOrderContext context) {

    }

    @Override
    public void conflict(AbstractOrderContext context) {

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
}
