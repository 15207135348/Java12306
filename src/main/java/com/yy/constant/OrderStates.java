package com.yy.constant;

public enum OrderStates {

    //抢票中
    RUSHING,
    //休息中
    SLEEPING,
    //已取消
    CANCELED,
    //抢票失败
    FAILED,
    //提交中
    SUBMITTING,
    //待支付
    TO_BE_PAID,
    //支付超时
    PAY_TIMEOUT,
    //已完成
    COMPLETED
}
