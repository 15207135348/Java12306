package com.yy.exception;

public class UnLoginException extends Exception {

    //12306账户
    private String username;
    private String password;

    public UnLoginException(String username, String password) {
        super(String.format("用户 %s 尚未登录", username));
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
