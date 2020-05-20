package com.yy.statemachine.realtime;

import com.yy.statemachine.OrderState;
import com.yy.statemachine.realtime.states.*;


public interface RealTimeOrderState extends OrderState {
    /**
     * 定义state
     */
    RealTimeRushingState realTimeRushingState = new RealTimeRushingState();
    RealTimeSubmittingState realTimeSubmittingState = new RealTimeSubmittingState();
    RealTimePayingState realTimePayingState = new RealTimePayingState();

}
