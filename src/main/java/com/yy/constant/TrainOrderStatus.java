package com.yy.constant;

public enum TrainOrderStatus {

    NO_COMPLETE("待支付", "等待用户支付"),
    PAY_TIMEOUT("支付超时", "未在可支付时间内付款"),
    COMPLETED("已完成", "在可支付时间内付款成功");

    private String status;
    private String desc;

    TrainOrderStatus(String status, String desc) {
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
