package com.yy.statemachine.realtime.states;

import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.realtime.RealTimeOrderAction;
import com.yy.statemachine.realtime.RealTimeOrderState;


/**
 * 抢票中状态下的行为
 */
public class RealTimeRushingState implements RealTimeOrderState {


    @Override
    public void entry(AbstractOrderContext context) {
        if (!context.isRunning()){
            return;
        }
        RealTimeOrderAction realTimeAction = (RealTimeOrderAction) context.getAction();
        //更新订单状态
        realTimeAction.update(context);
        //阻塞，直到找到余票或者可以候补
        if(realTimeAction.findRealTime(context)){
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

        context.setState(realTimeSubmittingState);

    }

    @Override
    public void submitSuccess(AbstractOrderContext context) {

    }

    @Override
    public void submitFailed(AbstractOrderContext context) {

    }

    @Override
    public void paySuccess(AbstractOrderContext context) {

    }

    @Override
    public void payFailed(AbstractOrderContext context) {

    }



}
