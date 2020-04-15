package com.yy.constant;

public enum UserOrderStatus {

    SUCCESS("抢票成功", "为客户抢到票，但是还需要支付"),
    RUSHING("抢票中", "正在刷票"),
    SLEEPING("休息中", "12306休息中"),
    EXPIRED("抢票失败","到截止日期未抢到票"),
    CANCELED("已取消", "用户自己取消订单"),

    AN_SUCCESS("候补抢票成功","为客户抢到候补车票，还需要继续抢票"),
    AN_RUSHING("候补抢票中","为客户抢到候补车票，还需要继续抢票");



    private String status;
    private String desc;


    UserOrderStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
