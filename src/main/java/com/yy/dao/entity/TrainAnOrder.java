package com.yy.dao.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 12306火车票订单
 * 是抢票成功后才有的订单
 */
@Entity
@Table(name = "train_an_order")
public class TrainAnOrder {

    @Id
    @GeneratedValue
    private int id;
    //候补订单号
    @Column(name = "reserve_no")
    private String reserveNo;
    //下单时间
    @Column(name = "order_time")
    private Timestamp orderTime;
    //车次信息
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
    @Column(name = "seat_type")
    private String seatType;
    //候补人数
    @Column(name = "queue_info")
    private String queueInfo;
    //总的支付金额
    @Column(name = "prepay_amount")
    private String prepayAmount;
    //订单状态
    @Column(name = "status")
    private String status;
    @Column(name = "passengers")
    private String passengers;

    @Column(name = "user_order_id")
    private String userOrderId;

    public TrainAnOrder() {
    }

    public TrainAnOrder(String reserveNo, Timestamp orderTime, String trainCode, String fromStation, String toStation, Timestamp fromTime, Timestamp toTime, String duration, String seatType, String queueInfo, String prepayAmount, String status, String passengers, String userOrderId) {
        this.reserveNo = reserveNo;
        this.orderTime = orderTime;
        this.trainCode = trainCode;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.duration = duration;
        this.seatType = seatType;
        this.queueInfo = queueInfo;
        this.prepayAmount = prepayAmount;
        this.status = status;
        this.passengers = passengers;
        this.userOrderId = userOrderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReserveNo() {
        return reserveNo;
    }

    public void setReserveNo(String reserveNo) {
        this.reserveNo = reserveNo;
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

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getQueueInfo() {
        return queueInfo;
    }

    public void setQueueInfo(String queueInfo) {
        this.queueInfo = queueInfo;
    }

    public String getPrepayAmount() {
        return prepayAmount;
    }

    public void setPrepayAmount(String prepayAmount) {
        this.prepayAmount = prepayAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassengers() {
        return passengers;
    }

    public void setPassengers(String passengers) {
        this.passengers = passengers;
    }

    public String getUserOrderId() {
        return userOrderId;
    }

    public void setUserOrderId(String userOrderId) {
        this.userOrderId = userOrderId;
    }
}
