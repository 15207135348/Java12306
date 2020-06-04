package com.yy.other.domain;

import java.util.List;

public class FindResult {

    private boolean found;

    private String date;
    //匹配到的车票
    private Train train;
    //匹配到到坐席
    private List<String> seats;

    public FindResult(boolean found, String date, Train train, List<String> seats) {
        this.found = found;
        this.date = date;
        this.train = train;
        this.seats = seats;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }
}
