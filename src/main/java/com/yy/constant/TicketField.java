package com.yy.constant;

/**
 * 余票查询时每个字段等含义
 */
public enum TicketField {

    SECRET_STR(0, "车票密钥，下单时用"),
    REMARK(1, "备注（预定/列车停运/几点起售）"),
    TRAIN_NO(2, "列车号(240000G41501)，查票价时用"),
    TRAIN_CODE(3, "车次(G415)"),
    START_STATION_CODE(4, "起始站代码"),
    END_STATION_CODE(5, "终点站代码"),
    FROM_STATION_CODE(6, "出发站代码"),
    TO_STATION_CODE(7, "到达站代码"),
    FROM_TIME(8, "出发时间"),
    TO_TIME(9, "到达时间"),
    DURATION(10, "历经多久"),
    CAN_BUY(11, "是否可以购买(Y/N)"),
    START_DATE(13, "列车起始站发车日期(20200123)"),
    TRAIN_LOCATION(15, "train_location"),
    FROM_STATION_NO(16, "出发站站序 01表示始发站，大于1表示过站"),
    TO_STATION_NO(17, "到达站序，对应火车经停的站序"),
    IS_SUPPORT_CARD(18, "可否使用二代身份证进出站 1可以，0不可以"),
    HIGH_SOFT_SLEEP_COUNT(21, "高级软卧等数量"),
    OTHER(22, "其它"),
    SOFT_SLEEP_COUNT(23, "软卧/一等卧数量"),
    SOFT_SEAT_COUNT(24, "软座数量"),
    SPECIAL_SEAT_COUNT(25, "特等座数量"),
    NONE_SEAT_COUNT(26, "无座数量"),
    YB_COUNT(27, "无座数量"),
    HARD_SLEEP_COUNT(28, "硬卧/二等卧数量"),
    HARD_SEAT_COUNT(29, "硬座数量"),
    SECOND_SEAT_COUNT(30, "二等座数量"),
    FIRST_SEAT_COUNT(31, "一等座数量"),
    BUSINESS_SEAT_COUNT(32, "商务座/特等座数量"),
    MOTOR_SLEEP_COUNT(33, "动卧数量"),
    SEAT_TYPES(35, "查询车票价格时的字段"),
    CAN_BACKUP(37, "是否支持候补,0不支持，1支持");

    private int i;
    private String desc;

    TicketField(int i, String desc) {
        this.i = i;
        this.desc = desc;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
