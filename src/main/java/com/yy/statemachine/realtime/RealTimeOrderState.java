package com.yy.statemachine.realtime;

import com.yy.statemachine.OrderState;
import com.yy.statemachine.realtime.states.*;


public interface RealTimeOrderState extends OrderState {
    /**
     * 定义state
     */
    RealTimeRushingState realTimeRushingState = new RealTimeRushingState("实时抢票中");
    RealTimeSubmittingState realTimeSubmittingState = new RealTimeSubmittingState("实时提交中");
    RealTimePayingState realTimePayingState = new RealTimePayingState("实时待支付");

}
