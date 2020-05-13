package com.yy.statemachine;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractOrderState implements OrderState {

    private String stateName;

    private static final Map<String, AbstractOrderState> map = new HashMap<>();

    public AbstractOrderState(String stateName) {
        this.stateName = stateName;
        map.put(stateName, this);
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public static AbstractOrderState getState(String stateName){
        return map.get(stateName);
    }
}
