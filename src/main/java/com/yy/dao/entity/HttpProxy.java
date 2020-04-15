package com.yy.dao.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "http_proxy")
public class HttpProxy {

    @Id
    @GeneratedValue
    private int id;
    @Column(name = "ip")
    private String ip;
    @Column(name = "port")
    private int port;
    @Column(name = "is_https")
    private boolean isHttps;
    @Column(name = "is_anonymous")
    private boolean isAnonymous;
    @Column(name = "speed")
    private double speed;
    @Column(name = "conn_time")
    private double connTime;
    //上一次验证可用的时间
    @Column(name = "check_time")
    private Timestamp checkTime;

    public HttpProxy() {
    }

    public HttpProxy(String ip, int port, boolean isHttps, boolean isAnonymous, double speed, double connTime, Timestamp checkTime) {
        this.ip = ip;
        this.port = port;
        this.isHttps = isHttps;
        this.isAnonymous = isAnonymous;
        this.speed = speed;
        this.connTime = connTime;
        this.checkTime = checkTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isHttps() {
        return isHttps;
    }

    public void setHttps(boolean https) {
        isHttps = https;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getConnTime() {
        return connTime;
    }

    public void setConnTime(double connTime) {
        this.connTime = connTime;
    }

    public Timestamp getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Timestamp checkTime) {
        this.checkTime = checkTime;
    }

    @Override
    public String toString() {
        return "HttpProxy{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", isHttps=" + isHttps +
                ", isAnonymous=" + isAnonymous +
                ", speed=" + speed +
                ", connTime=" + connTime +
                ", checkTime=" + checkTime +
                '}';
    }
}
