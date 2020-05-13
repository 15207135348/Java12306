package com.yy.observer;


import java.util.HashMap;
import java.util.Map;

public class Subject<T> {

    protected final Map<String, Observer<T>> observers = new HashMap<>();

    public void notifyObservers(T data) {
        for (Observer<T> observer : observers.values()) {
            observer.onMessage(data);
        }
    }

    public void notifyObserver(String observerID, T data) {
        Observer<T> observer = observers.get(observerID);
        if (observer != null) {
            observer.onMessage(data);
        }
    }

    public void attach(Observer<T> observer) {
        observers.put(observer.getId(), observer);
    }

    public void detach(Observer<T> observer) {
        observers.remove(observer.getId());
    }

    public void detach(String observerID) {
        observers.remove(observerID);
    }
}
