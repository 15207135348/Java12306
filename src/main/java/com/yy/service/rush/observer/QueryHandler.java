package com.yy.service.rush.observer;


import com.yy.integration.rail.TicketInquirer;
import com.yy.other.domain.HttpSession;
import com.yy.other.domain.Train;
import com.yy.other.factory.SessionFactory;
import com.yy.other.factory.ThreadPoolFactory;
import com.yy.common.util.SleepUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueryHandler extends Subject<QueryResult> implements Runnable {

    private final Logger LOGGER = Logger.getLogger(QueryHandler.class);

    private static final int[] SLEEP_TIME = new int[]{10000, 8000, 6000, 4000, 2000};

    private static void sleep(int priority) {
        SleepUtil.sleepRandomTime(SLEEP_TIME[priority + 1], SLEEP_TIME[priority]);
    }

    private String date;
    private String fromStation;
    private String toStation;
    private int priority;
    private String id;

    public String getDate() {
        return date;
    }

    private AtomicBoolean running = new AtomicBoolean(false);

    public String getId() {
        return id;
    }

    public QueryHandler(String id, String date, String fromStation, String toStation, int priority) {
        this.id = id;
        this.date = date;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.priority = priority;
    }


    @Override
    public void run() {
        while (running.get() && !observers.isEmpty()) {
            try {
                //查询一次火车票
                HttpSession session = SessionFactory.getSession(null);
                List<Train> trainList = TicketInquirer.getTrains(session, date, fromStation, toStation, true);
                sleep(priority);
                //如果查询失败
                if (trainList == null) {
                    SessionFactory.remove(null);
                    trainList = new ArrayList<>();
                }
                notifyObservers(new QueryResult(trainList, date));
            } catch (Exception e) {
                LOGGER.error(e);
            }
        }
        running.set(false);
        QueryHandlerManager.remove(this);
    }

    public void start(){
        if (!running.get()){
            running.set(true);
            ThreadPoolFactory.getThreadPool().execute(this);
        }
    }

    public void stop() {
        running.set(false);
    }

}
