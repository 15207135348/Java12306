package com.yy.service.rush;


import com.yy.dao.repository.UserOrderRepository;
import com.yy.dao.repository.WxAccountRepository;
import com.yy.dao.entity.WxAccount;
import com.yy.service.rushmanager.BlackRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderAction {

    @Autowired
    protected UserOrderRepository userOrderRepository;
    @Autowired
    protected WxAccountRepository wxAccountRepository;
    @Autowired
    protected BlackRoom blackRoom;

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

    public void addToBlackRoom(AbstractOrderContext context){
        blackRoom.add(context);
    }

}


