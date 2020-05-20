package com.yy.statemachine;

import com.yy.dao.entity.UserOrder;
import com.yy.dao.entity.WxAccount;
import com.yy.factory.ThreadPoolFactory;
import com.yy.domain.FindResult;

import java.util.concurrent.atomic.AtomicBoolean;


public abstract class AbstractOrderContext implements Runnable {

    protected AtomicBoolean running = new AtomicBoolean(false);

    //外部线程修改状态机状态时，为了让内部线程注意到专状态的变化，需要用volatile保证可见性
    protected volatile OrderState state;
    //状态机行为
    protected OrderAction action;
    //用户订单
    protected UserOrder order;
    //查询到的符合要求的结果
    protected FindResult findResult;
    //12306用户名和密码
    protected WxAccount wxAccount;
    protected int submitFailedCount = 0;

    public AbstractOrderContext(UserOrder userOrder, OrderAction action) {
        this.order = userOrder;
        this.action = action;
        action.initContext(this);
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

    public void stop() {
        this.running.set(false);
    }

    public boolean isRunning(){
        return running.get();
    }

    public WxAccount getWxAccount() {
        return wxAccount;
    }

    public void setWxAccount(WxAccount wxAccount) {
        this.wxAccount = wxAccount;
    }

    public int getSubmitFailedCount() {
        return submitFailedCount;
    }

    public void setSubmitFailedCount(int submitFailedCount) {
        this.submitFailedCount = submitFailedCount;
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
        running.set(true);
        this.setState(state);
    }

}
