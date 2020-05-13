package com.yy.statemachine.alternate;

import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.OrderState;
import com.yy.statemachine.alternate.states.*;


public interface AlternateOrderState extends OrderState {
    /**
     * 定义state
     */
    AlternateRushingState alternateRushingState = new AlternateRushingState("候补抢票中");
    AlternateSubmittingState alternateSubmittingState = new AlternateSubmittingState("候补提交中");
    AlternatePayingState alternatePayingState = new AlternatePayingState("候补待支付");
    CashingState cashingState = new CashingState("待兑现");

    //候补兑现成功
    void cashSuccess(AbstractOrderContext context);

    //候补兑现失败
    void cashFailed(AbstractOrderContext context);
}
