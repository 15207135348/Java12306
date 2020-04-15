package com.yy.dao.entity;

import javax.persistence.*;

@Entity
@Table(name = "train_ticket_price")
public class TrainTicketPrice {

    @Id
    @GeneratedValue
    private int id;
    @Column(name = "train_code")
    private String trainCode;
    @Column(name = "from_station")
    private String fromStation;
    @Column(name = "to_station")
    private String toStation;
    //最低的票价
    @Column(name = "lowest_price")
    private String lowestPrice;
    //json字符串，坐席类型对应票价
    @Column(name = "prices")
    private String prices;

    public TrainTicketPrice() {
    }

    public TrainTicketPrice(String trainCode, String fromStation, String toStation, String lowestPrice, String prices) {
        this.trainCode = trainCode;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.lowestPrice = lowestPrice;
        this.prices = prices;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(String lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }
}
