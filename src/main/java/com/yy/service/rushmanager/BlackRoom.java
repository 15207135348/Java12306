package com.yy.service.rushmanager;

import com.yy.service.rush.AbstractOrderContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


@Service
@EnableScheduling
public class BlackRoom {


    //保存待在小黑屋0-1分钟的状态机
    private static Queue<AbstractOrderContext> curr = new ArrayBlockingQueue<>(100);
    //保存待在小黑屋1-2分钟的状态机
    private static Queue<AbstractOrderContext> last = new ArrayBlockingQueue<>(100);

    @Autowired
    OrderContextManager orderContextManager;

    /**
     * 加入小黑屋
     *
     * @param orderContext
     */
    public void add(AbstractOrderContext orderContext) {
        curr.add(orderContext);
    }

    /**
     * 1到2分钟后重新启动状态机
     */
    @Scheduled(fixedRate = 60000)
    private void restart() {
        //启动1-2分钟的状态机
        AbstractOrderContext orderContext;
        while (!last.isEmpty()) {
            orderContext = last.poll();
            if (orderContext != null){
                orderContext.start();
            }
        }
        //0-1分钟的状态机变成1-2分钟的状态机
        while (!curr.isEmpty()){
            last.add(curr.poll());
        }
    }
}
