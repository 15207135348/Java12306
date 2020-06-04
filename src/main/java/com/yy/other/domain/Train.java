package com.yy.other.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 某趟火车在某个时刻各种车票的数量
 */
public class Train implements JSONAble {

    //G415
    private String trainCode;
    //列车的secretStr，下单的时候需要用到，在0号位置
    private String secretStr;
    //备注信息（预定/列车停运/几点起售），在1号位置
    private String remark;
    //是否可以候补
    private boolean canBackup;
    //出发站
    private String fromStation;
    //终点站
    private String toStation;
    //出发日期
    private String fromDate;
    //到达日期
    private String toDate;
    //出发时间
    private String fromTime;
    //到达时间
    private String toTime;
    //历时多久
    private String duration;
    //列车各种座位类型及其对应的票数(有/无/数字/--/空)
    private Map<String, String> tickets;
    //该车拥有的座位类型及其对应的价格
    private Map<String, String> prices;
    //最低价格的车票
    private String lowestPrice;

    private String other;

    private String trainNo;
    private String fromStationNo;
    private String toStationNo;
    private String seatTypes;

    public Train(){

    }


    public String getTicketCount(String seatType){
        return tickets.get(seatType);
    }

    public void setTicketCount(String seatType, String count)
    {
        if (tickets == null){
            tickets = new HashMap<>();
        }
        tickets.put(seatType, count);
    }

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public String getSecretStr() {
        return secretStr;
    }

    public void setSecretStr(String secretStr) {
        this.secretStr = secretStr;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isCanBackup() {
        return canBackup;
    }

    public void setCanBackup(boolean canBackup) {
        this.canBackup = canBackup;
    }

    public Map<String, String> getTickets() {
        return tickets;
    }

    public void setTickets(Map<String, String> tickets) {
        this.tickets = tickets;
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

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Map<String, String> getPrices() {
        return prices;
    }

    public void setPrices(Map<String, String> prices) {
        this.prices = prices;
    }

    public String getLowestPrice() {
        return lowestPrice;
    }

    public void setLowestPrice(String lowestPrice) {
        this.lowestPrice = lowestPrice;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public String getFromStationNo() {
        return fromStationNo;
    }

    public void setFromStationNo(String fromStationNo) {
        this.fromStationNo = fromStationNo;
    }

    public String getToStationNo() {
        return toStationNo;
    }

    public void setToStationNo(String toStationNo) {
        this.toStationNo = toStationNo;
    }

    public String getSeatTypes() {
        return seatTypes;
    }

    public void setSeatTypes(String seatTypes) {
        this.seatTypes = seatTypes;
    }
}
