package com.yy.integration.rail;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yy.integration.API12306;
import com.yy.dao.entity.Passenger;
import com.yy.dao.entity.TrainAnOrder;
import com.yy.dao.entity.TrainOrder;
import com.yy.dao.entity.TrainTicket;
import com.yy.other.domain.HttpSession;
import com.yy.other.domain.Train;
import com.yy.other.factory.SessionFactory;
import com.yy.common.util.TimeFormatUtil;
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

        HttpSession session = SessionFactory.getSession(username);
        //确保用户登录
        if (!Login12306.confirmLogin(username, password)) {
            LOGGER.error("findUnpaidOrder：登陆失败");
            return null;
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
     *
     * @param username    12306账户名
     * @param password    12306密码
     * @param train       抢到的车票对应的列车
     * @param userOrderId 用户订单ID
     * @return
     */
    public static TrainAnOrder findUnpaidAnOrder(String username, String password, Train train, String userOrderId) {
        HttpSession session = SessionFactory.getSession(username);
        //确保用户登录
        if (!Login12306.confirmLogin(username, password)) {
            LOGGER.error("findUnpaidAnOrder：登陆失败");
            return null;
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


    /**
     * 查询未出行的订单
     *
     * @param username
     * @param password
     * @param sequenceNo 订单号
     * @return
     */
    public static boolean isUntraveledOrder(String username, String password, String sequenceNo) {

        HttpSession session = SessionFactory.getSession(username);
        //确保用户登录
        if (!Login12306.confirmLogin(username, password)) {
            LOGGER.error("isUntraveledOrder：登陆失败");
            return false;
        }
        JSONObject jsonObject = API12306.queryMyUntraveledOrder(session);
        if (jsonObject == null || !jsonObject.containsKey("data")) {
            return false;
        }
        jsonObject = jsonObject.getJSONObject("data");
        if (!jsonObject.containsKey("OrderDTODataList")) {
            return false;
        }
        JSONArray orderDBList = jsonObject.getJSONArray("OrderDTODataList");
        if (orderDBList.isEmpty()) {
            return false;
        }

        for (int i = 0; i < orderDBList.size(); ++i) {
            JSONObject obj = orderDBList.getJSONObject(i);
            String temp = obj.getString("sequence_no");
            if (sequenceNo.equals(temp)) {
                return true;
            }
        }
        return false;
    }

    public static boolean cancelNoCompleteOrder(String username, String password, String sequenceNo) {
        HttpSession session = SessionFactory.getSession(username);
        //确保用户登录
        if (!Login12306.confirmLogin(username, password)) {
            LOGGER.error("cancelNoCompleteOrder：登陆失败");
            return false;
        }
        return API12306.cancelNoCompleteOrder(session, sequenceNo);
    }

    public static boolean isUnHonourHOrder(String username, String password, String reserveNo) {
        HttpSession session = SessionFactory.getSession(username);
        //确保用户登录
        if (!Login12306.confirmLogin(username, password)) {
            LOGGER.error("isUnHonourHOrder：登陆失败");
            return false;
        }
        JSONObject jsonObject = API12306.queryMyUnChashAnOrder(session);
        if (jsonObject == null || !jsonObject.containsKey("data")) {
            return false;
        }
        jsonObject = jsonObject.getJSONObject("data");
        if (!jsonObject.containsKey("list")) {
            return false;
        }
        JSONArray list = jsonObject.getJSONArray("list");
        if (list.isEmpty()) {
            return false;
        }

        for (int i = 0; i < list.size(); ++i) {
            JSONObject obj = list.getJSONObject(i);
            String temp = obj.getString("reserve_no");
            if (reserveNo.equals(temp)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取已兑现的候补订单ID，如果未兑现，返回null
     *
     * @param username
     * @param password
     * @param reserveNo
     * @return 订单编号 sequence_no
     */
    public static String getCashedHOrderID(String username, String password, String reserveNo) {
        HttpSession session = SessionFactory.getSession(username);
        //确保用户登录
        if (!Login12306.confirmLogin(username, password)) {
            LOGGER.error("getCashedHOrderID：登陆失败");
            return null;
        }
        JSONObject jsonObject = API12306.queryProcessedHOrder(session);
        if (jsonObject == null || !jsonObject.containsKey("data")) {
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        if (!jsonObject.containsKey("list")) {
            return null;
        }
        JSONArray list = jsonObject.getJSONArray("list");
        if (list.isEmpty()) {
            return null;
        }

        for (int i = 0; i < list.size(); ++i) {
            JSONObject obj = list.getJSONObject(i);
            String temp = obj.getString("reserve_no");
            String sequenceNo = obj.getString("sequence_no");
            if (reserveNo.equals(temp)) {
                return sequenceNo;
            }
        }
        return null;
    }

    /**
     * 查询未出行的订单
     *
     * @param username
     * @param password
     * @param sequenceNo
     * @param train
     * @return
     */
    public static Result findUntraveledOrder(String username, String password, String sequenceNo, TrainAnOrder train) {

        HttpSession session = SessionFactory.getSession(username);
        //确保用户登录
        if (!Login12306.confirmLogin(username, password)) {
            LOGGER.error("findUntraveledOrder：登陆失败");
            return null;
        }
        JSONObject jsonObject = API12306.queryMyUntraveledOrder(session);
        if (jsonObject == null || !jsonObject.containsKey("data")) {
            return null;
        }
        jsonObject = jsonObject.getJSONObject("data");
        if (!jsonObject.containsKey("OrderDTODataList")) {
            return null;
        }
        JSONArray orderDBList = jsonObject.getJSONArray("OrderDTODataList");
        if (orderDBList.isEmpty()) {
            return null;
        }
        JSONObject orderDB = null;
        for (int i = 0; i < orderDBList.size(); ++i) {
            orderDB = orderDBList.getJSONObject(i);
            if (sequenceNo.equals(orderDB.getString("sequence_no"))) {
                break;
            }
        }
        JSONArray array = orderDB.getJSONArray("tickets");
        if (array == null) {
            return null;
        }
        TrainOrder trainOrder = null;
        List<TrainTicket> tickets = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); ++i) {
            JSONObject obj = array.getJSONObject(i);
            if (i == 0) {
                long orderTime = TimeFormatUtil.dateToStamp(obj.getString("reserve_time"));
                String ticketStatus = obj.getString("ticket_status_name");
                trainOrder = new TrainOrder(sequenceNo, new Timestamp(orderTime),
                        train.getTrainCode(), train.getFromStation(), train.getToStation(),
                        train.getFromTime(), train.getToTime(),
                        train.getDuration(), ticketStatus, train.getUserOrderId());
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
                    passengerIdType, coachName, seatName, seatType, price, train.getUserOrderId());
            tickets.add(trainTicket);
        }
        return new Result(trainOrder, tickets);
    }


    /**
     * 取消未支付的候补订单
     *
     * @param username
     * @param password
     * @param reserveNo
     * @return
     */
    public static boolean cancelNoPaidAnOrder(String username, String password, String reserveNo) {
        HttpSession session = SessionFactory.getSession(username);
        //确保用户登录
        if (!Login12306.confirmLogin(username, password)) {
            LOGGER.error("cancelNoPaidAnOrder：登陆失败");
            return false;
        }
        JSONObject res = API12306.cancelNoPaidAnOrder(session, reserveNo);
        if (res == null || !res.containsKey("data")) {
            return false;
        }
        return res.getJSONObject("data").getBoolean("flag");
    }

    /**
     * 取消未兑现的候补订单
     *
     * @param username
     * @param password
     * @param reserveNo
     * @return 退款金额
     */
    public static String cancelNoCashAnOrder(String username, String password, String reserveNo) {
        HttpSession session = SessionFactory.getSession(username);
        //确保用户登录
        if (!Login12306.confirmLogin(username, password)) {
            LOGGER.error("cancelNoCashAnOrder：登陆失败");
            return null;
        }

        JSONObject res = API12306.reserveReturnCheck(session, reserveNo);
        if (res == null || !res.containsKey("data")) {
            return null;
        }
        if (!res.getJSONObject("data").getBoolean("flag")) {
            return null;
        }
        res = API12306.reserveReturn(session, reserveNo);
        if (res == null || !res.containsKey("data")) {
            return null;
        }
        if (!res.getJSONObject("data").getBoolean("flag")) {
            return null;
        }
        res = API12306.reserveReturnSuccessApi(session);
        if (res == null || !res.containsKey("data")) {
            return null;
        }
        String amount = res.getJSONObject("data").getString("amount");
        LOGGER.info(String.format("退款金额:%s", amount));
        return amount;
    }
}
