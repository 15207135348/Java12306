package com.yy.exception;

public class NetworkException extends Exception{

    public NetworkException(String msg) {
        super(String.format("HTTP请求失败【%s】", msg));
    }
}
