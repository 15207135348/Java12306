package com.yy.statemachine.alternate;

import com.yy.dao.entity.UserOrder;
import com.yy.factory.ThreadPoolFactory;
import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.OrderAction;


public class AlternateOrderContext extends AbstractOrderContext {


    public AlternateOrderContext(UserOrder userOrder, OrderAction action) {
        super(userOrder, action);
    }

    public void start(){
        this.state = AlternateOrderState.alternateRushingState;
        ThreadPoolFactory.getThreadPool().execute(this);
    }

}
