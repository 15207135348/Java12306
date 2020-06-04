package com.yy.other.constant;

public enum RushType {


    REAL_TIME("实时抢票"),
    ALTERNATE("候补抢票"),
    DUAL_CHANNEL("双通道抢票");

    private String type;


    RushType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static RushType get(String type) {
        for (RushType rushType : RushType.values()) {
            if (rushType.getType().equals(type)) {
                return rushType;
            }
        }
        return null;
    }
}
