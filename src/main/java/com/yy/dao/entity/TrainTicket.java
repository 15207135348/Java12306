package com.yy.dao.entity;

import com.yy.domain.JsonAble;

import javax.persistence.*;

@Entity
@Table(name = "train_ticket")
public class TrainTicket implements JsonAble {
    @Id
    @GeneratedValue
    private int id;
    //车票号
    @Column(name = "ticket_no")
    private String ticketNo;
    //票的类型（成人票/学生票）
    @Column(name = "ticket_type")
    private String ticketType;

    //乘客名
    @Column(name = "passenger_name")
    private String passengerName;
    //乘客证件号
    @Column(name = "passenger_id")
    private String passengerId;
    //乘客证件类型
    @Column(name = "passenger_id_type")
    private String passengerIdType;

    //车厢名(01/02)
    @Column(name = "coach")
    private String coach;
    //座位名(05C号)
    @Column(name = "seat")
    private String seat;
    //座位类型（一等座/硬卧）
    @Column(name = "seat_type")
    private String seatType;
    //车票价格
    @Column(name = "price")
    private String price;

    //该车票属于哪个火车票订单
    @Column(name = "user_order_id")
    private String userOrderId;


    public TrainTicket() {
    }

    public TrainTicket(String ticketNo, String ticketType, String passengerName, String passengerId, String passengerIdType, String coach, String seat, String seatType, String price, String userOrderId) {
        this.ticketNo = ticketNo;
        this.ticketType = ticketType;
        this.passengerName = passengerName;
        this.passengerId = passengerId;
        this.passengerIdType = passengerIdType;
        this.coach = coach;
        this.seat = seat;
        this.seatType = seatType;
        this.price = price;
        this.userOrderId = userOrderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerIdType() {
        return passengerIdType;
    }

    public void setPassengerIdType(String passengerIdType) {
        this.passengerIdType = passengerIdType;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserOrderId() {
        return userOrderId;
    }

    public void setUserOrderId(String userOrderId) {
        this.userOrderId = userOrderId;
    }
}
