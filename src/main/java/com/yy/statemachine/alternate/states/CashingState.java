package com.yy.statemachine.alternate.states;


import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.alternate.AbstractAlternateOrderState;
import com.yy.statemachine.alternate.AlternateOrderAction;

public class CashingState extends AbstractAlternateOrderState {

    public CashingState(String stateName) {
        super(stateName);
    }

    @Override
    public void entry(AbstractOrderContext context) {
        //更新订单状态
        AlternateOrderAction action = (AlternateOrderAction) context.getAction();
        action.update(context);
        //监测是否兑现成功
        if (action.checkCash(context)){
            cashSuccess(context);
        }else {
            cashFailed(context);
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
