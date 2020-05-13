package com.yy.observer;

import com.yy.dao.entity.UserOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueryHandlerManager {

    private static final Map<String, QueryHandler> queryHandlerMap = new ConcurrentHashMap<>();

    public static List<QueryHandler> getHandlers(UserOrder order) {
        List<QueryHandler> list = new ArrayList<>();
        String[] dates = order.getDates().split("/");
        for (String date : dates) {
            String handlerID = String.format("%s-%s-%s-%d", date,
                    order.getFromStation(), order.getToStation(), order.getPriority());
            //获取查询线程
            QueryHandler handler = queryHandlerMap.get(handlerID);
            if (handler == null) {
                handler = new QueryHandler(handlerID, date, order.getFromStation(), order.getToStation(), order.getPriority());
                queryHandlerMap.put(handlerID, handler);
            }
            list.add(handler);
        }
        return list;
    }

    public static void remove(QueryHandler queryHandler) {
        queryHandlerMap.remove(queryHandler.getId());
    }

    public static void add(QueryHandler queryHandler) {
        queryHandlerMap.put(queryHandler.getId(), queryHandler);
    }

}
