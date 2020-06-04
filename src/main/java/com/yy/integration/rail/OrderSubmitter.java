package com.yy.integration.rail;

import com.alibaba.fastjson.JSONObject;

import com.yy.integration.API12306;
import com.yy.other.domain.Passenger;
import com.yy.other.domain.HttpSession;
import com.yy.other.exception.UnfinishedOrderException;
import com.yy.other.factory.SessionFactory;
import com.yy.common.util.SleepUtil;
import com.yy.common.util.TimeFormatUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class OrderSubmitter {

    private static final Logger LOGGER = Logger.getLogger(OrderSubmitter.class);

    private static List<Passenger> findPassengers(List<Passenger> passengerList, List<String> targetNames) {
        List<Passenger> passengers = new ArrayList<>();
        for (Passenger passenger : passengerList) {
            String name = passenger.getPassenger_name();
            for (String target : targetNames) {
                if (name.equals(target)) {
                    passengers.add(passenger);
                }
            }
        }
        return passengers;
    }

    /**
     * 提交普通订单
     *
     * @param username       12306用户名
     * @param password       12306密码
     * @param secretStr      车票密钥
     * @param trainDate      开车日期
     * @param fromStation    出发站
     * @param toStation      到达站
     * @param passengerNames 乘客姓名
     * @param seatTypes      乘客的座位类型
     * @return 取票号
     */
    public static String submitRealTime(String username,
                                        String password,
                                        String secretStr,
                                        String trainDate,
                                        String fromStation,
                                        String toStation,
                                        List<String> passengerNames,
                                        List<String> seatTypes)
            throws UnfinishedOrderException {

        HttpSession session = SessionFactory.getSession(username);
        try {
            //确保用户登录
            if (!Login12306.confirmLogin(username, password)) {
                LOGGER.error("submitRealTime：登陆失败");
                return null;
            }
            //提交下单请求
            boolean success = API12306.submitOrderRequest(session, secretStr, trainDate, TimeFormatUtil.currentDate(), fromStation, toStation);
            if (!success) {
                LOGGER.warn("submitOrderRequest失败");
                return null;
            }
            SleepUtil.sleepRandomTime(500, 1000);
            //获取联系人
            JSONObject object = API12306.getTokenAndTicket(session);
            List<Passenger> passengers = API12306.getPassengersWithToken(session, object.getString("token"));
            if (passengers == null) {
                LOGGER.warn("getPassengersWithToken失败");
                return null;
            }
            List<Passenger> passengerList = findPassengers(passengers, passengerNames);
            if (passengerList.isEmpty()) {
                LOGGER.warn("找不到选择的乘客");
                return null;
            }
            SleepUtil.sleepRandomTime(500, 1000);

            //检查选票信息
            String passengerTicketStr = API12306.Helper.getMultiPassengerTicketStr(passengerList, seatTypes);
            String oldPassengerStr = API12306.Helper.getMultiOldPassengerStr(passengerList);
            success = API12306.checkOrderInfo(session, object.getString("token"), passengerTicketStr, oldPassengerStr);
            if (!success) {
                LOGGER.warn("checkOrderInfo失败");
                return null;
            }
            SleepUtil.sleepRandomTime(500, 1000);

            //获取排队人数
            JSONObject data = API12306.getQueueCount(session, object, seatTypes.get(0), trainDate);
            LOGGER.info(data);
            //确认提交订单
            success = API12306.confirmSingleForQueue(session, object, passengerTicketStr, oldPassengerStr);
            if (!success) {
                LOGGER.warn("confirmSingleForQueue下单失败");
                return null;
            }
            SleepUtil.sleepRandomTime(500, 1000);
            //订单结果
            String orderId = null;
            for (int i = 0; i <= 5; ++i) {
                orderId = API12306.queryOrderWaitTime(session, object.getString("token"));
                if (orderId != null) {
                    break;
                }
                SleepUtil.sleepRandomTime(500, 1000);
            }
            if (orderId == null) {
                LOGGER.warn("queryOrderWaitTime获取订单ID失败");
                return null;
            }
            //检查订单提交是否成功
            success = API12306.resultOrderForDcQueue(session, orderId, object.getString("token"));
            if (!success) {
                LOGGER.warn("resultOrderForDcQueue检查订单没有成功");
                return null;
            }
            return orderId;
        } catch (UnfinishedOrderException e) {
            LOGGER.warn(e.getMessage());
            throw e;
        }
    }


    /**
     * 提交候补订单
     *
     * @param username       12306用户名
     * @param password       12306密码
     * @param secretStr      车次的密钥（当前只限定一种车次，12306规则是一个候补订单可以选择两种【车次+座位】的组合）
     * @param seatTypes      选择的座位类型，座位类型数量和乘客数量一致
     * @param passengerNames 选择的乘客，每个12306账户最多只能有一个候补订单，候补订单最多可以有3个乘客
     * @return 是否成功
     */
    public static boolean submitAfterNate(String username,
                                          String password,
                                          String secretStr,
                                          List<String> seatTypes,
                                          List<String> passengerNames)
            throws UnfinishedOrderException {

        //有不订单一次最多只能有3个乘客
        while (passengerNames.size() > 3) {
            passengerNames.remove(passengerNames.size() - 1);
            seatTypes.remove(seatTypes.size() - 1);
            LOGGER.warn("用户数量大于3，移除后面的用户");
        }
        HttpSession session = SessionFactory.getSession(username);
        try {
            //确保用户登录
            if (!Login12306.confirmLogin(username, password)) {
                LOGGER.error("submitAfterNate：登陆失败");
                return false;
            }
            SleepUtil.sleepRandomTime(500, 1000);
            boolean success = API12306.checkFace(session, secretStr, seatTypes.get(0));
            if (!success) {
                LOGGER.warn("checkFace失败");
                return false;
            }
            SleepUtil.sleepRandomTime(500, 1000);
            JSONObject res = API12306.getSuccessRate(session, secretStr, seatTypes.get(0));
            if (res == null || !res.getBoolean("status")) {
                LOGGER.warn("submitANOrderRequest失败");
                return false;
            }
            SleepUtil.sleepRandomTime(500, 1000);
            res = res.getJSONObject("data").getJSONArray("flag").getJSONObject(0);
            String trainNo = res.getString("train_no");
            String count = res.getString("info");//候补人数
            LOGGER.info(String.format("提交候补订单，当前候补人数：【%s】", count));
            success = API12306.submitANOrderRequest(session, secretStr, seatTypes.get(0));
            if (!success) {
                LOGGER.warn("submitANOrderRequest失败");
                return false;
            }
            SleepUtil.sleepRandomTime(500, 1000);
            //获取联系人
            res = API12306.getTokenAndTicket(session);
            List<Passenger> passengerList = API12306.getPassengersWithToken(session, res.getString("token"));
            if (passengerList == null) {
                LOGGER.warn("getPassengersWithToken失败");
                return false;
            }
            List<Passenger> passengers = findPassengers(passengerList, passengerNames);
            if (passengers.isEmpty()) {
                LOGGER.warn("找不到该乘客");
                return false;
            }

            SleepUtil.sleepRandomTime(500, 1000);
            res = API12306.initPassengerApi(session);
            if (res == null || !res.getBoolean("status")) {
                LOGGER.warn("initPassengerApi失败");
                return false;
            }
            String jzdhDateE = res.getJSONObject("data").getString("jzdhDateE");
            String jzdhHourE = res.getJSONObject("data").getString("jzdhHourE");
            if (jzdhDateE == null || jzdhHourE == null) {
                LOGGER.warn(String.format("获取当前候补日期失败,原因：%s", res.getJSONArray("messages").toJSONString()));
                return false;
            }
            String jzParam = jzdhDateE + "#" + jzdhHourE.replaceAll(":", "#");
            //验证候补订单
            StringBuilder passengerInfo = new StringBuilder();
            for (Passenger passenger : passengers) {
                passengerInfo.append(String.format("1#%s#1#%s#%s;", passenger.getPassenger_name(),
                        passenger.getPassenger_id_no(), passenger.getAllEncStr()));
            }
            SleepUtil.sleepRandomTime(500, 1000);
            success = API12306.confirmHB(session, passengerInfo.toString(), jzParam, trainNo, seatTypes.get(0));
            if (!success) {
                LOGGER.warn("confirmHB失败");
                return false;
            }
            //候补排队
            SleepUtil.sleepRandomTime(500, 1000);
            success = API12306.queryANQueue(session);
            if (!success) {
                LOGGER.warn("queryANQueue失败");
                return false;
            }
        } catch (UnfinishedOrderException e) {
            throw e;
        }
        return true;
    }
}
