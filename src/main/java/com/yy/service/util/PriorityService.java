package com.yy.service.util;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PriorityService {

    private Random random = new Random();
    private static final int[] SLEEP_TIME = new int[]{10000, 8000, 6000, 4000, 2000};

    private int getIntBetween(int min, int max){
        return random.nextInt(max - min) + min;
    }

    public void sleepRandomTime(int min, int max){
        try {
            Thread.sleep(getIntBetween(min, max));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sleep(int priority){
        sleepRandomTime(SLEEP_TIME[priority+1], SLEEP_TIME[priority]);
    }
}
