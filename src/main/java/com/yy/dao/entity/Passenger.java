package com.yy.dao.entity;

import com.yy.domain.JsonAble;
import javax.persistence.*;

@Entity
@Table(name = "passenger")
public class Passenger implements JsonAble {
    @Id
    @GeneratedValue
    private int id;
    //乘客姓名
    @Column(name = "name")
    private String name;
    //成人/学生/儿童
    @Column(name = "type")
    private String type;
    //身份证号/军官证号
    @Column(name = "id_no")
    private String idNo;
    //二代身份证/军官证
    @Column(name = "id_type")
    private String idType;
    //12306账户名
    @Column(name = "username")
    private String username;
    //手机是否核验
    @Column(name = "if_receive")
    private boolean ifReceive;
    //手机是否核验
    @Column(name = "mobile")
    private String mobile;


    public Passenger() {
    }

    public Passenger(String name, String type, String idNo, String idType, String username, boolean ifReceive, String mobile) {
        this.name = name;
        this.type = type;
        this.idNo = idNo;
        this.idType = idType;
        this.username = username;
        this.ifReceive = ifReceive;
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isIfReceive() {
        return ifReceive;
    }

    public void setIfReceive(boolean ifReceive) {
        this.ifReceive = ifReceive;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
