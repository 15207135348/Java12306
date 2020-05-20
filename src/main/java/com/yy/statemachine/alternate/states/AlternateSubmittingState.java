package com.yy.statemachine.alternate.states;


import com.yy.exception.UnfinishedOrderException;
import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.alternate.AlternateOrderAction;
import com.yy.statemachine.alternate.AlternateOrderState;

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
        context.setSubmitFailedCount(context.getSubmitFailedCount());
        //如果提交失败的次数大于5次，则结束
        if (context.getSubmitFailedCount() > 5){
            context.setState(failedState);
        }else {
            context.setState(alternateRushingState);
        }
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
