package com.yy.util;

import org.apache.log4j.Logger;

public class SleepUtil {

    private static final Logger LOGGER = Logger.getLogger(SleepUtil.class);
    public static void sleep(int millis)
    {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
