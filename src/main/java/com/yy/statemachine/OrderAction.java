package com.yy.statemachine;


import com.yy.dao.UserOrderRepository;
import com.yy.dao.WxAccountRepository;
import com.yy.dao.entity.WxAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderAction {

    @Autowired
    protected UserOrderRepository userOrderRepository;
    @Autowired
    protected WxAccountRepository wxAccountRepository;

    public boolean update(AbstractOrderContext context) {
        OrderState state = context.getState();
        String name = OrderStateManager.getStateName(state);
        context.getOrder().setStatus(name);
        userOrderRepository.save(context.getOrder());
        return true;
    }

    public boolean initContext(AbstractOrderContext context){
        WxAccount wxAccount = wxAccountRepository.findByOpenId(context.getOrder().getOpenId());
        context.setWxAccount(wxAccount);
        return true;
    }

    public boolean addQueryCount(AbstractOrderContext context){
        context.getOrder().setQueryCount(context.getOrder().getQueryCount()+1);
        userOrderRepository.addAndGetQueryCountByOrderId(context.getOrder().getOrderId(), 1);
        return true;
    }


    public boolean notifyFinished(AbstractOrderContext context) {

        return true;
    }


    public boolean notifyFailed(AbstractOrderContext context) {

        return true;
    }

}


