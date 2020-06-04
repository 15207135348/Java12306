package com.yy.service.rush.alternate;

import com.yy.dao.entity.UserOrder;
import com.yy.other.factory.ThreadPoolFactory;
import com.yy.service.rush.AbstractOrderContext;
import com.yy.service.rush.OrderAction;


public class AlternateOrderContext extends AbstractOrderContext {


    public AlternateOrderContext(UserOrder userOrder, OrderAction action) {
        super(userOrder, action);
    }

    public void start(){
        this.state = AlternateOrderState.alternateRushingState;
        ThreadPoolFactory.getThreadPool().execute(this);
    }

}
