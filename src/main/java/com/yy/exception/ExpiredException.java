package com.yy.exception;

public class ExpiredException extends RuntimeException{

    public ExpiredException(String orderId) {
        super(String.format("订单【%s】已过期", orderId));
    }
}
