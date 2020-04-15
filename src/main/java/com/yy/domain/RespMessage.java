package com.yy.domain;

public class RespMessage {
    // 请求是否成功
    private boolean success;
    // 消息体
    private String message;

    public RespMessage() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RespMessage{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
