package com.yy.other.exception;

public class UnfinishedOrderException extends Exception{

    public UnfinishedOrderException() {
        super("还有未处理的订单");
    }
}
