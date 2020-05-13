package com.yy.statemachine.realtime;


import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRealTimeOrderState implements RealTimeOrderState {

    private String stateName;

    private static final Map<String, AbstractRealTimeOrderState> map = new HashMap<>();

    public AbstractRealTimeOrderState(String stateName) {
        this.stateName = stateName;
        map.put(stateName, this);
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public static AbstractRealTimeOrderState getState(String stateName){
        return map.get(stateName);
    }
}
