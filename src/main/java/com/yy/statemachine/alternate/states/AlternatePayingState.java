package com.yy.statemachine.alternate.states;


import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.alternate.AbstractAlternateOrderState;
import com.yy.statemachine.alternate.AlternateOrderAction;

public class AlternatePayingState extends AbstractAlternateOrderState {


    public AlternatePayingState(String stateName) {
        super(stateName);
    }

    @Override
    public void entry(AbstractOrderContext context) {
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
            payFailed(context);
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
