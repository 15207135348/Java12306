package com.yy.dao.entity;

import javax.persistence.*;

@Entity
@Table(name = "wx_account")
public class WxAccount {
    @Id
    @GeneratedValue
    private int id;
    //用户的微信信息
    @Column(name = "open_id")
    private String openId;
    @Column(name = "session_key")
    private String sessionKey;
    @Column(name = "union_id")
    private String unionId;
    //12306用户密码
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    //用户的优先级
    @Column(name = "priority")
    private int priority;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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