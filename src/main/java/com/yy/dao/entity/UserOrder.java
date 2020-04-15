package com.yy.dao.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yy.domain.JsonAble;
import com.yy.util.TimeFormatUtil;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_order")
public class UserOrder implements JsonAble {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "order_id")
    private String orderId;

    //用户提交的订单信息
    @Column(name = "from_station")
    private String fromStation;
    @Column(name = "to_station")
    private String toStation;
    @Column(name = "dates")
    private String dates;
    @Column(name = "trains")
    private String trains;
    @Column(name = "seats")
    private String seats;
    @Column(name = "people")
    private String people;
    @Column(name = "contact_info")
    private String contactInfo;
    @Column(name = "rush_type")
    private String rushType;
    @Column(name = "priority")
    private int priority;

    //下单时间
    @Column(name = "order_time")
    private Timestamp orderTime;
    //过期时间
    @Column(name = "expire_time")
    private Timestamp expireTime;
    //订单状态
    @Column(name = "status")
    private String status;
    //当前的刷票次数
    @Column(name = "query_count")
    private int queryCount;

    //用于查询某个微信账户下的订单
    @Column(name = "open_id")
    private String openId;


    public UserOrder() {

    }

    public UserOrder(String orderId, String fromStation, String toStation, String dates, String trains, String seats, String people, String contactInfo, String rushType, int priority, Timestamp orderTime, Timestamp expireTime, String status, int queryCount, String openId) {
        this.orderId = orderId;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.dates = dates;
        this.trains = trains;
        this.seats = seats;
        this.people = people;
        this.contactInfo = contactInfo;
        this.rushType = rushType;
        this.priority = priority;
        this.orderTime = orderTime;
        this.expireTime = expireTime;
        this.status = status;
        this.queryCount = queryCount;
        this.openId = openId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getFromStation() {
        return fromStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public void setToStation(String toStation) {
        this.toStation = toStation;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getTrains() {
        return trains;
    }

    public void setTrains(String trains) {
        this.trains = trains;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Timestamp expireTime) {
        this.expireTime = expireTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(int queryCount) {
        this.queryCount = queryCount;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getRushType() {
        return rushType;
    }

    public void setRushType(String rushType) {
        this.rushType = rushType;
    }

    @Override
    public Object toJSON() {
        JSONObject jsonObject = (JSONObject) JSON.toJSON(this);
        jsonObject.put("orderTime", orderTime.toLocalDateTime());
        jsonObject.put("expireTime", TimeFormatUtil.stamp2Date(expireTime.getTime(), "MM月dd日 HH:mm"));
        return jsonObject;
    }
}