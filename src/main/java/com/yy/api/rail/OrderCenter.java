package com.yy.api.rail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yy.api.API12306;
import com.yy.dao.entity.Passenger;
import com.yy.dao.entity.TrainAnOrder;
import com.yy.dao.entity.TrainOrder;
import com.yy.dao.entity.TrainTicket;
import com.yy.domain.Session;
import com.yy.domain.Train;
import com.yy.factory.SessionFactory;
import com.yy.util.SleepUtil;
import com.yy.util.TimeFormatUtil;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 对应12306官网的订单中心
 * <p>
 * 普通订单
 * -未完成订单查询
 * -未出行订单查询
 * -历史订单查询
 * <p>
 * 候补订单
 * -待支付订单查询
 * -待兑现订单查询
 * -已处理订单查询
 */
public class OrderCenter {


    private static final Logger LOGGER = Logger.getLogger(OrderCenter.class);


    public static class Result {
        private TrainOrder trainOrder;
        private List<TrainTicket> tickets;

        Result(TrainOrder trainOrder, List<TrainTicket> tickets) {
            this.trainOrder = trainOrder;
            this.tickets = tickets;
        }

        public TrainOrder getTrainOrder() {
            return trainOrder;
        }

        public List<TrainTicket> getTickets() {
            return tickets;
        }
    }

    /**
     * 普通订单：查询未支付的普通订单
     *
     * @param username    12306账户名
     * @param password    12306密码
     * @param train       抢到的车票对应的列车
     * @param userOrderId 用户订单ID
     * @return
     */
    public static Result findUnpaidOrder(String username, String password, Train train, String userOrderId) {

        Session session = SessionFactory.getSession(username);
        //检查用户是否登陆成功
        boolean success = API12306.checkLogin(session);
        if (!success) {
            Login12306.login(username, password);
            SleepUtil.sleepRandomTime(1000, 2000);
            success = API12306.checkLogin(session);
            if (!success) {
                return null;
            }
        }
        JSONObject jsonObject = API12306.queryNoCompleteOrder(session);
        if (jsonObject == null || !jsonObject.containsKey("data")) {
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        if (!jsonObject.containsKey("orderDBList")) {
            return null;
        }
        JSONArray orderDBList = jsonObject.getJSONArray("orderDBList");
        if (orderDBList.isEmpty()) {
            return null;
        }
        JSONArray array = orderDBList.getJSONObject(0).getJSONArray("tickets");
        if (array == null) {
            return null;
        }
        TrainOrder trainOrder = null;
        List<TrainTicket> tickets = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); ++i) {
            JSONObject obj = array.getJSONObject(i);
            if (i == 0) {
                String sequenceNo = obj.getString("sequence_no");
                long orderTime = TimeFormatUtil.dateToStamp(obj.getString("reserve_time"));
                String ticketStatus = obj.getString("ticket_status_name");
                trainOrder = new TrainOrder(sequenceNo, new Timestamp(orderTime),
                        train.getTrainCode(), train.getFromStation(), train.getToStation(),
                        new Timestamp(TimeFormatUtil.date2Stamp(train.getFromDate() + " " + train.getFromTime(), "yyyyMMdd HH:mm")),
                        new Timestamp(TimeFormatUtil.date2Stamp(train.getToDate() + " " + train.getToTime(), "yyyyMMdd HH:mm")),
                        train.getDuration(), ticketStatus, userOrderId);
            }
            //乘客
            JSONObject passengerDTO = obj.getJSONObject("passengerDTO");
            String passengerName = passengerDTO.getString("passenger_name");
            String passengerIdType = passengerDTO.getString("passenger_id_type_name");
            String passengerId = passengerDTO.getString("passenger_id_no");
            //票的信息
            String coachName = obj.getString("coach_name");
            String seatName = obj.getString("seat_name");
            String seatType = obj.getString("seat_type_name");
            String ticketType = obj.getString("ticket_type_name");
            String price = obj.getString("str_ticket_price_page");
            String ticketNo = obj.getString("ticket_no");

            TrainTicket trainTicket = new TrainTicket(ticketNo, ticketType, passengerName, passengerId,
                    passengerIdType, coachName, seatName, seatType, price, userOrderId);
            tickets.add(trainTicket);
        }
        return new Result(trainOrder, tickets);
    }


    /**
     * 查询未支付的候补订单
     * @param username    12306账户名
     * @param password    12306密码
     * @param train       抢到的车票对应的列车
     * @param userOrderId 用户订单ID
     * @return
     */
    public static TrainAnOrder findUnpaidAnOrder(String username, String password, Train train, String userOrderId) {
        Session session = SessionFactory.getSession(username);
        //检查用户是否登陆成功
        boolean success = API12306.checkLogin(session);
        if (!success) {
            Login12306.login(username, password);
            SleepUtil.sleepRandomTime(1000, 2000);
            success = API12306.checkLogin(session);
            if (!success) {
                LOGGER.error("queryTrainOrder：登陆失败，获取不到火车订单信息");
                return null;
            }
        }
        JSONObject jsonObject = API12306.queryNoCompleteAnOrder(session);
        if (jsonObject == null || !jsonObject.containsKey("data")) {
            return null;
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (!data.containsKey("order")) {
            return null;
        }
        JSONObject order = data.getJSONObject("order");
        JSONArray needs = order.getJSONArray("needs");
        if (needs.size() != 1) {
            LOGGER.info("queryTrainAnOrder:候补车票信息错误");
            return null;
        }
        String reserveNo = order.getString("reserve_no");
        String prepayAmount = order.getString("prepay_amount");
        String status = order.getString("status_name");

        JSONObject need = needs.getJSONObject(0);
        String seatType = need.getString("seat_name");
        String queueInfo = need.getString("queue_info");
        JSONArray array = order.getJSONArray("passengers");
        JSONArray passengers = new JSONArray();
        for (int i = 0; i < array.size(); ++i) {
            JSONObject p = array.getJSONObject(i);
            String name = p.getString("passenger_name");
            String type = p.getString("ticket_type");
            String idType = p.getString("passenger_id_name");
            String idNo = p.getString("passenger_id_no");
            Passenger passenger = new Passenger(name, type, idNo, idType, username, false, "");
            passengers.add(passenger.toJSON());
        }
        return new TrainAnOrder(reserveNo,
                new Timestamp(System.currentTimeMillis()), train.getTrainCode(), train.getFromStation(), train.getToStation(),
                new Timestamp(TimeFormatUtil.date2Stamp(train.getFromDate() + " " + train.getFromTime(), "yyyyMMdd HH:mm")),
                new Timestamp(TimeFormatUtil.date2Stamp(train.getToDate() + " " + train.getToTime(), "yyyyMMdd HH:mm")),
                train.getDuration(), seatType, queueInfo, prepayAmount, status, passengers.toJSONString(), userOrderId);
    }
}
