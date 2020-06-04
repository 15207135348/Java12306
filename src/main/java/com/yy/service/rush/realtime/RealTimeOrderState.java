package com.yy.service.rush.realtime;

import com.yy.service.rush.OrderState;
import com.yy.service.rush.realtime.states.*;


public interface RealTimeOrderState extends OrderState {
    /**
     * 定义state
     */
    RealTimeRushingState realTimeRushingState = new RealTimeRushingState();
    RealTimeSubmittingState realTimeSubmittingState = new RealTimeSubmittingState();
    RealTimePayingState realTimePayingState = new RealTimePayingState();

}
