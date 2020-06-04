package com.yy.service.rush.alternate.states;


import com.yy.other.exception.UnfinishedOrderException;
import com.yy.service.rush.AbstractOrderContext;
import com.yy.service.rush.alternate.AlternateOrderAction;
import com.yy.service.rush.alternate.AlternateOrderState;

public class AlternateSubmittingState implements AlternateOrderState {


    @Override
    public void entry(AbstractOrderContext context) {

        if (!context.isRunning()){
            return;
        }
        AlternateOrderAction alternateAction = (AlternateOrderAction) context.getAction();
        try {
            if (alternateAction.submitAlternate(context)) {
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
        context.setState(alternatePayingState);
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
