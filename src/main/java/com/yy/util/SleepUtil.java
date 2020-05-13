package com.yy.util;

import org.apache.log4j.Logger;

import java.util.Random;

public class SleepUtil {

    private static final Logger LOGGER = Logger.getLogger(SleepUtil.class);

    private static final Random random = new Random();


    public static void sleep(int millis)
    {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void sleepRandomTime(int min, int max){
        try {
            Thread.sleep(random.nextInt(max - min) + min);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
