package com.yy.constant;

import java.util.HashMap;
import java.util.Map;

public enum  SeatType {

    //商务座/特等座
    BUSINESS_SEAT(32, "9", "A9","商务座"),
    FIRST_SEAT(31, "M", "M","一等座"),
    SECOND_SEAT(30, "O", "O","二等座"),
    SOFT_SEAT(24, "2", "A2","软座"),
    HARD_SEAT(29, "1", "A1","硬座"),
    NONE_SEAT(26, "1", "WZ","无座"),
    HIGH_SOFT_SLEEP(21, "6", "A6","高级软卧"),
    SOFT_SLEEP(23, "4", "A4","软卧"),
    MOTOR_SLEEP(27, "F", "F","动卧"),
    HARD_SLEEP(28, "3", "A3", "硬卧");

    private int i;
    private String no;
    private String code;
    private String name;
    private static Map<String, SeatType> map = new HashMap<>();

    static {
        map.put(BUSINESS_SEAT.name, BUSINESS_SEAT);
        map.put(FIRST_SEAT.name, FIRST_SEAT);
        map.put(SECOND_SEAT.name, SECOND_SEAT);
        map.put(SOFT_SEAT.name, SOFT_SEAT);
        map.put(HARD_SEAT.name, HARD_SEAT);
        map.put(NONE_SEAT.name, NONE_SEAT);
        map.put(HIGH_SOFT_SLEEP.name, HIGH_SOFT_SLEEP);
        map.put(SOFT_SLEEP.name, SOFT_SLEEP);
        map.put(MOTOR_SLEEP.name, MOTOR_SLEEP);
        map.put(HARD_SLEEP.name, HARD_SLEEP);

        map.put(BUSINESS_SEAT.code, BUSINESS_SEAT);
        map.put(FIRST_SEAT.code, FIRST_SEAT);
        map.put(SECOND_SEAT.code, SECOND_SEAT);
        map.put(SOFT_SEAT.code, SOFT_SEAT);
        map.put(HARD_SEAT.code, HARD_SEAT);
        map.put(NONE_SEAT.code, NONE_SEAT);
        map.put(HIGH_SOFT_SLEEP.code, HIGH_SOFT_SLEEP);
        map.put(SOFT_SLEEP.code, SOFT_SLEEP);
        map.put(MOTOR_SLEEP.code, MOTOR_SLEEP);
        map.put(HARD_SLEEP.code, HARD_SLEEP);


        map.put(BUSINESS_SEAT.no, BUSINESS_SEAT);
        map.put(FIRST_SEAT.no, FIRST_SEAT);
        map.put(SECOND_SEAT.no, SECOND_SEAT);
        map.put(SOFT_SEAT.no, SOFT_SEAT);
        map.put(HARD_SEAT.no, HARD_SEAT);
        map.put(NONE_SEAT.no, NONE_SEAT);
        map.put(HIGH_SOFT_SLEEP.no, HIGH_SOFT_SLEEP);
        map.put(SOFT_SLEEP.no, SOFT_SLEEP);
        map.put(MOTOR_SLEEP.no, MOTOR_SLEEP);
        map.put(HARD_SLEEP.no, HARD_SLEEP);
    }

    SeatType(int i, String no, String code, String name) {
        this.i = i;
        this.no = no;
        this.code = code;
        this.name = name;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static SeatType find(String name){
        return map.get(name);
    }

    public static int findI(String name){
        if (map.containsKey(name)){
            return map.get(name).i;
        }
        return -1;
    }

    public static String findNo(String name){
        if (map.containsKey(name)){
            return map.get(name).no;
        }
        return null;
    }
    public static String findCode(String name){
        if (map.containsKey(name)){
            return map.get(name).code;
        }
        return null;
    }

}
