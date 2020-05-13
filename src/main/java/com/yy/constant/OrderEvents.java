package com.yy.constant;

public enum OrderEvents {

    //23点睡觉
    SLEEP,
    //6点起床
    WAKEUP,
    //用户取消订单
    CANCEL,
    //订单过期了
    EXPIRE,
    //发现有余票
    FOUND_TICKET,
    //订单提交成功
    SUBMIT_SUCCESS,
    //提交订单失败
    SUBMIT_FAILED,
    //错误点订单
    ERROR_ORDER,
    //支付成功
    PAY_SUCCESS,
    //30分钟内未完成支付
    PAY_FAILED

}
