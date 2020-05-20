package com.yy.observer;

import com.yy.domain.Train;

import java.util.List;

public class QueryResult {
    private List<Train> trainList;
    private String date;

    public QueryResult(List<Train> trainList, String date) {
        this.trainList = trainList;
        this.date = date;
    }

    public List<Train> getTrainList() {
        return trainList;
    }

    public String getDate() {
        return date;
    }
}
