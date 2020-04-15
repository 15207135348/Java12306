package com.yy.dao.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "log")
public class Log {

    @Id
    @GeneratedValue
    private int id;
    @Column(name = "user")
    private String user;
    @Column(name = "method")
    private String method;
    @Column(name = "start")
    private Timestamp start;
    @Column(name = "end")
    private Timestamp end;
    @Column(name = "success")
    private boolean success;
    @Column(name = "caused")
    private String caused;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCaused() {
        return caused;
    }

    public void setCaused(String caused) {
        this.caused = caused;
    }
}
