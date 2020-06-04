package com.yy.service.rush.realtime.states;

import com.yy.other.exception.UnfinishedOrderException;
import com.yy.service.rush.AbstractOrderContext;
import com.yy.service.rush.realtime.RealTimeOrderAction;
import com.yy.service.rush.realtime.RealTimeOrderState;

public class RealTimeSubmittingState implements RealTimeOrderState {

    @Override
    public void entry(AbstractOrderContext context) {
        if (!context.isRunning()){
            return;
        }
        RealTimeOrderAction realTimeAction = (RealTimeOrderAction) context.getAction();
        try {
            if (realTimeAction.submitRealTime(context)) {
                submitSuccess(context);
            } else {
                submitFailed(context);
            }
        } catch (UnfinishedOrderException e) {
            conflict(context);
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
        context.setState(canceledState);
    }

    @Override
    public void expire(AbstractOrderContext context) {

    }

    @Override
    public void conflict(AbstractOrderContext context) {
        context.setState(conflictState);
    }

    @Override
    public void found(AbstractOrderContext context) {

    }

    @Override
    public void submitFailed(AbstractOrderContext context) {
        context.getAction().addToBlackRoom(context);
    }

    @Override
    public void submitSuccess(AbstractOrderContext context) {
        context.setState(realTimePayingState);
    }

    @Override
    public void paySuccess(AbstractOrderContext context) {

    }

    @Override
    public void payFailed(AbstractOrderContext context) {

    }
}
