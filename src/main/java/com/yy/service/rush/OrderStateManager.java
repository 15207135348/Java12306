package com.yy.service.rush;

import java.util.HashMap;
import java.util.Map;

import static com.yy.service.rush.alternate.AlternateOrderState.*;
import static com.yy.service.rush.realtime.RealTimeOrderState.*;

public class OrderStateManager {

    private static final Map<String, OrderState> orderStateMap1 = new HashMap<>();
    private static final Map<OrderState, String> orderStateMap2 = new HashMap<>();

    static {
        orderStateMap1.put("休息中", sleepingState);
        orderStateMap1.put("已取消", canceledState);
        orderStateMap1.put("抢票失败", failedState);
        orderStateMap1.put("已完成", finishedState);

        orderStateMap1.put("与未完成订单冲突", conflictState);
        orderStateMap1.put("实时抢票中", realTimeRushingState);
        orderStateMap1.put("实时提交中", realTimeSubmittingState);
        orderStateMap1.put("实时待支付", realTimePayingState);

        orderStateMap1.put("候补抢票中", alternateRushingState);
        orderStateMap1.put("候补提交中", alternateSubmittingState);
        orderStateMap1.put("候补待支付", alternatePayingState);
        orderStateMap1.put("待兑现", cashingState);
        for (String key : orderStateMap1.keySet()){
            orderStateMap2.put(orderStateMap1.get(key), key);
        }
    }


    public static String getStateName(OrderState orderState){
        return orderStateMap2.get(orderState);
    }

    public static OrderState getOrderState(String name){
        return orderStateMap1.get(name);
    }

}
