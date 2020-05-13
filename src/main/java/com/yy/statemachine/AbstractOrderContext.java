package com.yy.statemachine;

import com.yy.dao.entity.UserOrder;
import com.yy.factory.ThreadPoolFactory;
import com.yy.observer.FindResult;


public abstract class AbstractOrderContext implements Runnable {

    //外部线程修改状态机状态时，为了让内部线程注意到专状态的变化，需要用volatile保证可见性
    protected volatile OrderState state;
    //状态机行为
    protected OrderAction action;
    //用户订单
    protected UserOrder order;
    //查询到的符合要求的结果
    protected FindResult findResult;

    public AbstractOrderContext(UserOrder userOrder, OrderAction action) {
        this.order = userOrder;
        this.action = action;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        //出口动作
        if (this.state != null) {
            this.state.exit(this);
        }
        //更新状态
        this.state = state;
        //入口动作
        if (this.state != null) {
            this.state.entry(this);
        }
    }

    public OrderAction getAction() {
        return action;
    }

    public void setAction(OrderAction action) {
        this.action = action;
    }

    public UserOrder getOrder() {
        return order;
    }

    public void setOrder(UserOrder order) {
        this.order = order;
    }

    public FindResult getFindResult() {
        return findResult;
    }

    public void setFindResult(FindResult findResult) {
        this.findResult = findResult;
    }

    //设置某个状态启动
    public void start(OrderState state) {
        //如果是空值，启动失败
        if (state == null) {
            return;
        }
        this.state = state;
        ThreadPoolFactory.getThreadPool().execute(this);
    }

    public abstract void start();

    @Override
    public void run() {
        this.setState(state);
    }

}
