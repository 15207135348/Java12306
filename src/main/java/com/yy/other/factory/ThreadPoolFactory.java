package com.yy.other.factory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolFactory {

    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10,
            20, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20));

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }
}
