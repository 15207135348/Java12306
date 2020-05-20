package com.yy.statemachine;


import com.yy.statemachine.states.*;

public interface OrderState {

    SleepingState sleepingState = new SleepingState();
    CanceledState canceledState = new CanceledState();
    FailedState failedState = new FailedState();
    FinishedState finishedState = new FinishedState();
    ConflictState conflictState = new ConflictState();

    /**
     * 定义translation事件
     */
    //入口动作
    void entry(AbstractOrderContext context);

    //出口动作
    void exit(AbstractOrderContext context);

    //睡觉事件（定时器触发）
    void sleep(AbstractOrderContext context);

    //起床事件（定时器触发）
    void wakeup(AbstractOrderContext context);

    //取消订单事件（外部触发）
    void cancel(AbstractOrderContext context);

    //订单过期事件（内部触发）
    void expire(AbstractOrderContext context);

    //有余票，提交订单（内部触发）
    void found(AbstractOrderContext context);

    //与未完成的订单发生冲突事件
    void conflict(AbstractOrderContext context);

    //提交失败事件（内部触发）
    void submitFailed(AbstractOrderContext context);

    //提交成功事件（内部触发）
    void submitSuccess(AbstractOrderContext context);

    //支付成功事件（内部触发）
    void paySuccess(AbstractOrderContext context);

    //支付失败事件（内部触发）
    void payFailed(AbstractOrderContext context);
}
