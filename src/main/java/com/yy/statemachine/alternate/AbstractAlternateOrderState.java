package com.yy.statemachine.alternate;


import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAlternateOrderState implements AlternateOrderState {

    private String stateName;

    private static final Map<String, AbstractAlternateOrderState> map = new HashMap<>();

    public AbstractAlternateOrderState(String stateName) {
        this.stateName = stateName;
        map.put(stateName, this);
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public static AbstractAlternateOrderState getState(String stateName){
        return map.get(stateName);
    }
}
