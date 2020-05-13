package com.yy.statemachine;


import com.yy.dao.UserOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderAction {

    @Autowired
    protected UserOrderRepository userOrderRepository;

    public boolean update(AbstractOrderContext context) {
        OrderState state = context.getState();
        if (!(state instanceof AbstractOrderState)){
            return false;
        }
        context.getOrder().setStatus(((AbstractOrderState) state).getStateName());
        userOrderRepository.save(context.getOrder());
        return true;
    }


    public boolean notifyFinished(AbstractOrderContext context) {

        return true;
    }


    public boolean notifyFailed(AbstractOrderContext context) {

        return true;
    }

}


