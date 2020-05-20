package com.yy.statemachine.alternate;

import com.yy.statemachine.AbstractOrderContext;
import com.yy.statemachine.OrderState;
import com.yy.statemachine.alternate.states.*;


public interface AlternateOrderState extends OrderState {
    /**
     * 定义state
     */
    AlternateRushingState alternateRushingState = new AlternateRushingState();
    AlternateSubmittingState alternateSubmittingState = new AlternateSubmittingState();
    AlternatePayingState alternatePayingState = new AlternatePayingState();
    CashingState cashingState = new CashingState();

    //候补兑现成功
    void cashSuccess(AbstractOrderContext context);

    //候补兑现失败
    void cashFailed(AbstractOrderContext context);
}
