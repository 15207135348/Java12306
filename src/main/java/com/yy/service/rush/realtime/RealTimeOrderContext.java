package com.yy.service.rush.realtime;

import com.yy.dao.entity.UserOrder;
import com.yy.other.factory.ThreadPoolFactory;
import com.yy.service.rush.AbstractOrderContext;
import com.yy.service.rush.OrderAction;


public class RealTimeOrderContext extends AbstractOrderContext {

    public RealTimeOrderContext(UserOrder userOrder, OrderAction action) {
        super(userOrder, action);
    }

    public void start(){
        this.state = RealTimeOrderState.realTimeRushingState;
        ThreadPoolFactory.getThreadPool().execute(this);
    }
}
