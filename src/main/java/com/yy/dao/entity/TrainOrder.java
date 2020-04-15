package com.yy.dao.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 12306火车票订单
 * 是抢票成功后才有的订单
 */
@Entity
@Table(name = "train_order")
public class TrainOrder {

    @Id
    @GeneratedValue
    private int id;
    //订单序号
    @Column(name = "sequence_no")
    private String sequenceNo;
    //下单时间
    @Column(name = "order_time")
    private Timestamp orderTime;
    //订单详情
    @Column(name = "train_code")
    private String trainCode;
    @Column(name = "from_station")
    private String fromStation;
    @Column(name = "to_station")
    private String toStation;
    @Column(name = "from_time")
    private Timestamp fromTime;
    @Column(name = "to_time")
    private Timestamp toTime;
    @Column(name = "duration")
    private String duration;

    @Column(name = "status")
    private String status;

    @Column(name = "user_order_id")
    private String userOrderId;

    public TrainOrder() {
    }

    public TrainOrder(String sequenceNo, Timestamp orderTime, String trainCode, String fromStation, String toStation, Timestamp fromTime, Timestamp toTime, String duration, String status, String userOrderId) {
        this.sequenceNo = sequenceNo;
        this.orderTime = orderTime;
        this.trainCode = trainCode;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.duration = duration;
        this.status = status;
        this.userOrderId = userOrderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
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

    public Timestamp getFromTime() {
        return fromTime;
    }

    public void setFromTime(Timestamp fromTime) {
        this.fromTime = fromTime;
    }

    public Timestamp getToTime() {
        return toTime;
    }

    public void setToTime(Timestamp toTime) {
        this.toTime = toTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserOrderId() {
        return userOrderId;
    }

    public void setUserOrderId(String userOrderId) {
        this.userOrderId = userOrderId;
    }
}
