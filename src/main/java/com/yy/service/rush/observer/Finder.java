package com.yy.service.rush.observer;

import com.yy.other.domain.FindResult;
import com.yy.service.rush.AbstractOrderContext;
import com.yy.other.domain.Train;

import java.util.*;
import java.util.concurrent.SynchronousQueue;

public class Finder {


    private static FindResult waitResult(AbstractOrderContext orderContext, com.yy.service.rush.observer.Observer<QueryResult> observer,
                                         SynchronousQueue<FindResult> synchronousQueue) {
        //初始化查询线程（父类是Subject），没查一次票会通知一次观察者去匹配余票
        //一个订单按日期分为多个查询线程
        //一个查询线程中可查询多个订单
        List<QueryHandler> queryHandlers = QueryHandlerManager.getHandlers(orderContext.getOrder());
        for (QueryHandler handler : queryHandlers) {
            //订阅，查到一次票就回调
            observer.subscribe(handler);
            //启动查询线程
            handler.start();
        }

        //阻塞，直到获取到符合要求的查询结果
        FindResult result = null;
        try {
            result = synchronousQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //获取到了查询结果后，记得取消订阅，这样就不会再通知观察者了
            for (QueryHandler handler : queryHandlers) {
                //取消订阅
                observer.unsubscribe(handler);
            }
        }
        orderContext.setFindResult(result);
        return result;
    }


    /**
     * 根据用户订单异步查询有余票的车次+坐席
     * 如果没有找到，会一直阻塞
     *
     * @param orderContext 用户订单状态机
     * @return
     */
    public static FindResult findRealTime(AbstractOrderContext orderContext) {

        //初始化查询条件
        Set<String> trainCodes = new HashSet<>();
        Set<String> seats = new HashSet<>();
        List<String> people = new ArrayList<>();
        Collections.addAll(trainCodes, orderContext.getOrder().getTrains().split("/"));
        Collections.addAll(seats, orderContext.getOrder().getSeats().split("/"));
        Collections.addAll(people, orderContext.getOrder().getPeople().split("/"));

        //阻塞队列，用于接收符合要求的查询结果
        final SynchronousQueue<FindResult> synchronousQueue = new SynchronousQueue<>();

        //初始化观察者
        Observer<QueryResult> observer = new Observer<QueryResult>(orderContext.getOrder().getOrderId()) {
            @Override
            public void onMessage(QueryResult data) {
                //余票匹配算法
                FindResult result = findRealTime(data.getTrainList(), data.getDate(), trainCodes, seats, people.size());
                orderContext.getAction().addQueryCount(orderContext);
                //如果符合要求，就加入阻塞队列
                if (result.isFound()) {
                    try {
                        synchronousQueue.put(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //如果状态机已关闭，则向阻塞队列发送数据，使得退出阻塞状态
                if (!orderContext.isRunning()) {
                    try {
                        synchronousQueue.put(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //订阅主题，等待结果
        return waitResult(orderContext, observer, synchronousQueue);
    }

    /**
     * 根据用户订单异步查询可候补的车次+坐席
     * 如果没有找到，会一直阻塞
     *
     * @param orderContext 用户订单状态机
     * @return
     */
    public static FindResult findAlternate(AbstractOrderContext orderContext) {
        //初始化查询条件
        Set<String> trainCodes = new HashSet<>();
        Set<String> seats = new HashSet<>();
        List<String> people = new ArrayList<>();
        Collections.addAll(trainCodes, orderContext.getOrder().getTrains().split("/"));
        Collections.addAll(seats, orderContext.getOrder().getSeats().split("/"));
        Collections.addAll(people, orderContext.getOrder().getPeople().split("/"));

        //阻塞队列，用于接收符合要求的查询结果
        final SynchronousQueue<FindResult> synchronousQueue = new SynchronousQueue<>();

        //初始化观察者
        Observer<QueryResult> observer = new Observer<QueryResult>(orderContext.getOrder().getOrderId()) {
            @Override
            public void onMessage(QueryResult data) {
                //余票匹配算法
                FindResult result = findAlternate(data.getTrainList(), data.getDate(), trainCodes, seats, people.size());
                orderContext.getAction().addQueryCount(orderContext);
                //如果符合要求，就加入阻塞队列
                if (result.isFound()) {
                    try {
                        synchronousQueue.put(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //如果状态机已关闭，则向阻塞队列发送数据，使得退出阻塞状态
                if (!orderContext.isRunning()) {
                    try {
                        synchronousQueue.put(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //订阅主题，等待结果
        return waitResult(orderContext, observer, synchronousQueue);
    }


    /**
     * 找到有余票的车次+坐席
     *
     * @param trainList   车次列表
     * @param date        目标日期
     * @param trainCodes  目标车次
     * @param targetSeats 目标坐席
     * @param size        乘客数量
     * @return 匹配结果
     */
    private static FindResult findRealTime(List<Train> trainList, String date, Set<String> trainCodes, Set<String> targetSeats, int size) {
        //打乱匹配的顺序，防止因为某辆列车人数较多导致每次都下单失败
        Collections.shuffle(trainList);
        //按车次过滤
        filterTrainCode(trainCodes, trainList);
        int i;
        Train train = null;
        List<String> seats = null;
        boolean success = false;
        for (i = 0; i < trainList.size(); ++i) {
            train = trainList.get(i);
            //检查有没有同一种座位类型的车票
            seats = findSingleSeat(targetSeats, size, train);
            if (seats != null) {
                success = true;
                break;
            }
            //检查有没有不同座位类型的车票
            seats = findMultiSeat(targetSeats, size, train);
            if (seats != null) {
                success = true;
                break;
            }
        }
        return new FindResult(success, date, train, seats);
    }


    /**
     * 找到可候补的车次+坐席
     *
     * @param trainList   车次列表
     * @param date        目标日期
     * @param trainCodes  目标车次
     * @param targetSeats 目标坐席
     * @param size        乘客数量
     * @return 匹配结果
     */
    private static FindResult findAlternate(List<Train> trainList, String date, Set<String> trainCodes, Set<String> targetSeats, int size) {
        //打乱匹配的顺序，防止因为某辆列车人数较多导致每次都下单失败
        Collections.shuffle(trainList);
        //按车次过滤
        filterTrainCode(trainCodes, trainList);
        int i;
        Train train = null;
        List<String> seats = null;
        boolean success = false;
        for (i = 0; i < trainList.size(); ++i) {
            train = trainList.get(i);
            seats = findAlternateSeat(targetSeats, size, train);
            if (seats != null) {
                success = true;
                break;
            }
        }
        return new FindResult(success, date, train, seats);
    }


    private static void add(List<String> list, String e, int num) {
        for (int i = 0; i < num; ++i) {
            list.add(e);
        }
    }

    /**
     * 过滤掉不符合要求掉车次
     *
     * @param trainCodes 目标车次
     * @param trainList  所有的车次
     */
    private static void filterTrainCode(Set<String> trainCodes, List<Train> trainList) {
        for (int i = trainList.size() - 1; i >= 0; --i) {
            Train train = trainList.get(i);
            if (!trainCodes.contains(train.getTrainCode())) {
                trainList.remove(i);
            }
        }
    }


    /**
     * @param targetSeats 要找的坐席种类
     * @param size        要找的坐席数量
     * @param train       从该车次中查询
     * @return 找到的坐席
     */
    private static List<String> findSingleSeat(Set<String> targetSeats, int size, Train train) {
        List<String> seats;
        for (String s : targetSeats) {
            String count = train.getTicketCount(s);
            //如果有
            if (count.equals("有")) {
                seats = new ArrayList<>();
                add(seats, s, size);
                return seats;
            }
            //如果是具体的数量，且数量足够
            if (count.matches("^[1-9]\\d*$") && Integer.parseInt(count) >= size) {
                seats = new ArrayList<>();
                add(seats, s, size);
                return seats;
            }
        }
        return null;
    }


    /**
     * @param targetSeats 要找的坐席种类
     * @param size        要找的坐席数量
     * @param train       从该车次中查询
     * @return 找到的坐席
     */
    private static List<String> findMultiSeat(Set<String> targetSeats, int size, Train train) {
        List<String> seats = new ArrayList<>();
        for (String s : targetSeats) {
            String count = train.getTicketCount(s);
            //如果是有
            boolean success = count.matches("^[1-9]\\d*$");
            if (!success) {
                continue;
            }
            int num = Integer.parseInt(count);
            for (int i = 0; i < num; ++i) {
                seats.add(s);
                if (seats.size() == size) {
                    break;
                }
            }
        }
        if (seats.size() == size) {
            return seats;
        }
        return null;
    }


    /**
     * @param targetSeats 要找的坐席种类
     * @param size        要找的坐席数量
     * @param train       从该车次中查询
     * @return 找到的坐席
     */
    private static List<String> findAlternateSeat(Set<String> targetSeats, int size, Train train) {
        List<String> seats = null;
        //检查有没有候补车票
        for (String s : targetSeats) {
            String count = train.getTicketCount(s);
            //判断坐席是否可以候补
            boolean isCanBackup = count.equals("无") && train.isCanBackup();
            if (isCanBackup) {
                seats = new ArrayList<>();
                add(seats, s, size);
                break;
            }
        }
        return seats;
    }
}