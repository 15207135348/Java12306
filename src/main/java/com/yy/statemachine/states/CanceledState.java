package com.yy.statemachine.states;

import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.AbstractOrderState;
import com.yy.statemachine.OrderAction;

public class CanceledState extends AbstractOrderState {


    public CanceledState(String stateName) {
        super(stateName);
    }

    @Override
    public void entry(AbstractOrderContext context) {
        OrderAction action = context.getAction();
        //更新订单状态
        action.update(context);
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
}
