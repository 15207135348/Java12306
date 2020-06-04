package com.yy.other.exception;

public class UnkownOrderState extends Exception{

    public UnkownOrderState() {
        super("未知的订单状态，请检查订单状态是否正确");
    }
}
