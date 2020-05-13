package com.yy.statemachine.realtime;

import com.yy.dao.entity.UserOrder;
import com.yy.factory.ThreadPoolFactory;
import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.OrderAction;


public class RealTimeOrderContext extends AbstractOrderContext {

    public RealTimeOrderContext(UserOrder userOrder, OrderAction action) {
        super(userOrder, action);
    }

    public void start(){
        this.state = RealTimeOrderState.realTimeRushingState;
        ThreadPoolFactory.getThreadPool().execute(this);
    }
}
