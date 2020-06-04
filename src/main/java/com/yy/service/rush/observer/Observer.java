package com.yy.service.rush.observer;


public abstract class Observer<T> {

    private String id;

    public Observer(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void subscribe(Subject<T> subject) {
        subject.attach(this);
    }

    public void unsubscribe(Subject<T> subject) {
        subject.detach(this);
    }

    public abstract void onMessage(T data);
}
