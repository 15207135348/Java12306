package com.yy.service.rush.alternate;


import com.yy.dao.repository.TrainAnOrderRepository;
import com.yy.dao.repository.TrainOrderRepository;
import com.yy.dao.repository.TrainTicketRepository;
import com.yy.dao.repository.WxAccountRepository;
import com.yy.integration.rail.OrderCenter;
import com.yy.integration.rail.OrderSubmitter;
import com.yy.dao.entity.TrainAnOrder;
import com.yy.dao.entity.WxAccount;
import com.yy.other.exception.UnfinishedOrderException;
import com.yy.other.domain.FindResult;
import com.yy.service.rush.observer.Finder;
import com.yy.service.notification.SendMsgService;
import com.yy.service.rush.AbstractOrderContext;
import com.yy.service.rush.OrderAction;
import com.yy.service.rush.alternate.states.AlternatePayingState;
import com.yy.common.util.SleepUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AlternateOrderAction extends OrderAction {

    @Autowired
    private SendMsgService sendMsgService;
    @Autowired
    private WxAccountRepository wxAccountRepository;
    @Autowired
    private TrainAnOrderRepository trainAnOrderRepository;
    @Autowired
    private TrainTicketRepository trainTicketRepository;
    @Autowired
    private TrainOrderRepository trainOrderRepository;


    public boolean findAlternate(AbstractOrderContext context) {
        return Finder.findAlternate(context).isFound();
    }

    /**
     * 提交候补订单
     * @param context
     * @return
     */
    public boolean submitAlternate(AbstractOrderContext context) throws UnfinishedOrderException {
        FindResult findResult = context.getFindResult();
        if (findResult == null) {
            return false;
        }
        WxAccount wxAccount = wxAccountRepository.findByOpenId(context.getOrder().getOpenId());
        return OrderSubmitter.submitAfterNate(
                wxAccount.getUsername(),
                wxAccount.getPassword(),
                findResult.getTrain().getSecretStr(),
                findResult.getSeats(),
                Arrays.asList(context.getOrder().getPeople().split("/"))
        );
    }

    /**
     * 保存候补订单
     * @param context
     * @return
     */
    public boolean saveUnpaidAlternateOrder(AbstractOrderContext context) {
        WxAccount wxAccount = wxAccountRepository.findByOpenId(context.getOrder().getOpenId());

        FindResult findResult = context.getFindResult();
        if (findResult == null) {
            return false;
        }
        TrainAnOrder trainAnOrder = OrderCenter.findUnpaidAnOrder(wxAccount.getUsername(), wxAccount.getPassword(),
                findResult.getTrain(), context.getOrder().getOrderId());

        while (trainAnOrder == null) {
            SleepUtil.sleepRandomTime(1000, 2000);
            trainAnOrder = OrderCenter.findUnpaidAnOrder(wxAccount.getUsername(), wxAccount.getPassword(),
                    findResult.getTrain(), context.getOrder().getOrderId());
        }

        trainAnOrderRepository.save(trainAnOrder);

        return true;
    }


    /**
     * 通知前往支付候补订单
     * @param context
     * @return
     */
    public boolean notifyAlternatePay(AbstractOrderContext context) {
        TrainAnOrder trainAnOrder = trainAnOrderRepository.findByUserOrderId(context.getOrder().getOrderId());
        if (trainAnOrder == null) {
            return false;
        }
        if (!(context.getState() instanceof AlternatePayingState)) {
            return false;
        }
        sendMsgService.sendAlternate(context.getOrder());
        return true;
    }

    /**
     * 检查候补订单是否支付
     * @param context
     * @return
     */
    public boolean checkAlternatePay(AbstractOrderContext context) {

        WxAccount wxAccount = context.getWxAccount();
        String orderId = context.getOrder().getOrderId();
        TrainAnOrder trainAnOrder = trainAnOrderRepository.findByUserOrderId(orderId);
        boolean isUnHonourHOrder = false;
        long expiredTime = System.currentTimeMillis() + 30 * 60000;
        while (context.isRunning() && System.currentTimeMillis() < expiredTime) {
            isUnHonourHOrder = OrderCenter.isUnHonourHOrder(wxAccount.getUsername(), wxAccount.getPassword(), trainAnOrder.getReserveNo());
            if (isUnHonourHOrder) {
                break;
            }
            //30秒钟检测一次
            SleepUtil.sleep(30000);
        }
        return isUnHonourHOrder;
    }

    /**
     * 取消未支付的候补订单
     * @param context
     * @return
     */
    public boolean cancelUnpaidAlternateOrder(AbstractOrderContext context) {
        WxAccount wxAccount = context.getWxAccount();
        String orderId = context.getOrder().getOrderId();
        TrainAnOrder trainAnOrder = trainAnOrderRepository.findByUserOrderId(orderId);
        return OrderCenter.cancelNoPaidAnOrder(wxAccount.getUsername(), wxAccount.getPassword(), trainAnOrder.getReserveNo());
    }

    /**
     * 检查是否兑现成功
     * @param context
     * @return
     */
    public boolean checkCash(AbstractOrderContext context) {
        WxAccount wxAccount = context.getWxAccount();
        String orderId = context.getOrder().getOrderId();
        TrainAnOrder trainAnOrder = trainAnOrderRepository.findByUserOrderId(orderId);
        long expiredTime = context.getOrder().getExpireTime().getTime();
        String orderID = null;
        while (context.isRunning() && System.currentTimeMillis() < expiredTime) {
            orderID = OrderCenter.getCashedHOrderID(wxAccount.getUsername(), wxAccount.getPassword(), trainAnOrder.getReserveNo());
            if (orderID != null) {
                break;
            }
            //30秒钟检测一次
            SleepUtil.sleep(30000);
        }
        return orderID != null;
    }

    /**
     * 保存已兑现的订单
     * @param context
     * @return
     */
    public boolean saveCashedOrder(AbstractOrderContext context) {
        WxAccount wxAccount = context.getWxAccount();
        String orderId = context.getOrder().getOrderId();
        TrainAnOrder trainAnOrder = trainAnOrderRepository.findByUserOrderId(orderId);
        String orderID = OrderCenter.getCashedHOrderID(wxAccount.getUsername(), wxAccount.getPassword(), trainAnOrder.getReserveNo());
        if (orderID == null){
            return false;
        }
        OrderCenter.Result result = OrderCenter.findUntraveledOrder(wxAccount.getUsername(), wxAccount.getPassword(), orderID, trainAnOrder);
        if (result ==null){
            return false;
        }
        trainOrderRepository.save(result.getTrainOrder());
        trainTicketRepository.save(result.getTickets());
        return true;
    }


    /**
     * 取消未兑现的候补订单
     * @param context
     * @return 退款金额
     */
    public String cancelUncashedOrder(AbstractOrderContext context) {
        WxAccount wxAccount = context.getWxAccount();
        String orderId = context.getOrder().getOrderId();
        TrainAnOrder trainAnOrder = trainAnOrderRepository.findByUserOrderId(orderId);
        return OrderCenter.cancelNoCashAnOrder(wxAccount.getUsername(), wxAccount.getPassword(), trainAnOrder.getReserveNo());
    }

}


