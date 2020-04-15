package com.yy.service.core;

import com.alibaba.fastjson.JSONObject;
import com.yy.dao.WxAccountRepository;
import com.yy.dao.entity.WxAccount;
import com.yy.domain.Passenger;
import com.yy.domain.Session;
import com.yy.exception.UnfinishedOrderException;
import com.yy.service.util.SessionPoolService;
import com.yy.service.api.API12306Service;
import com.yy.service.util.PriorityService;
import com.yy.util.TimeFormatUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubmitOrderService {

    private static final Logger LOGGER = Logger.getLogger(SubmitOrderService.class);

    @Autowired
    private API12306Service api12306Service;
    @Autowired
    private SessionPoolService sessionPoolService;
    @Autowired
    private Login12306Service login12306Service;
    @Autowired
    private WxAccountRepository wxAccountRepository;
    @Autowired
    private PriorityService priorityService;

    private List<Passenger> findPassengers(List<Passenger> passengerList, List<String> targetNames) {
        List<Passenger> passengers = new ArrayList<>();
        for (Passenger passenger : passengerList) {
            String name = passenger.getPassenger_name();
            for (String target : targetNames){
                if (name.equals(target)){
                    passengers.add(passenger);
                }
            }
        }
        return passengers;
    }

    /**
     * 多个乘客下单
     *
     * @param openID        用户微信的openID
     * @param secretStr     车票密钥
     * @param trainDate     开车日期
     * @param fromStation   出发站
     * @param toStation     到达站
     * @param passengerNames 乘客姓名
     * @param seatTypes      乘客的座位类型
     * @return 取票号
     */
    public String submit(String openID,
                         String secretStr,
                         String trainDate,
                         String fromStation,
                         String toStation,
                         List<String> passengerNames,
                         List<String> seatTypes) {

        WxAccount wxAccount = wxAccountRepository.findByOpenId(openID);
        Session session = sessionPoolService.getSession(wxAccount.getUsername());
        try {
            //检查用户是否登陆成功
            boolean success = api12306Service.checkLogin(session);
            if (!success) {
                login12306Service.login(wxAccount.getUsername(), wxAccount.getPassword());
                priorityService.sleepRandomTime(1000, 2000);
                success = api12306Service.checkLogin(session);
                if (!success){
                    LOGGER.error("submit：登陆失败，提交订单失败");
                    return null;
                }
            }
            priorityService.sleepRandomTime(500, 1000);
            //提交下单请求
            success = api12306Service.submitOrderRequest(session, secretStr, trainDate, TimeFormatUtil.currentDate(), fromStation, toStation);
            if (!success) {
                LOGGER.warn("submitOrderRequest失败");
                return null;
            }
            priorityService.sleepRandomTime(500, 1000);
            //获取联系人
            JSONObject object = api12306Service.getTokenAndTicket(session);
            List<Passenger> passengers = api12306Service.getPassengersWithToken(session, object.getString("token"));
            if (passengers == null) {
                LOGGER.warn("getPassengersWithToken失败");
                return null;
            }
            List<Passenger> passengerList = findPassengers(passengers, passengerNames);
            if (passengerList.isEmpty()) {
                LOGGER.warn("找不到选择的乘客");
                return null;
            }
            priorityService.sleepRandomTime(500, 1000);

            //检查选票信息
            String passengerTicketStr = API12306Service.Helper.getMultiPassengerTicketStr(passengerList, seatTypes);
            String oldPassengerStr = API12306Service.Helper.getMultiOldPassengerStr(passengerList);
            success = api12306Service.checkOrderInfo(session, object.getString("token"), passengerTicketStr, oldPassengerStr);
            if (!success) {
                LOGGER.warn("checkOrderInfo失败");
                return null;
            }
            priorityService.sleepRandomTime(500, 1000);

            //获取排队人数
            JSONObject data = api12306Service.getQueueCount(session, object, seatTypes.get(0), trainDate);
            LOGGER.info(data);
            //确认提交订单
            success = api12306Service.confirmSingleForQueue(session, object, passengerTicketStr, oldPassengerStr);
            if (!success) {
                LOGGER.warn("confirmSingleForQueue下单失败");
                return null;
            }
            priorityService.sleepRandomTime(500, 1000);
            //订单结果
            String orderId = null;
            for (int i = 0; i <= passengerList.size() * 5; ++i)
            {
                orderId = api12306Service.queryOrderWaitTime(session, object.getString("token"));
                if (orderId != null){
                    break;
                }
                priorityService.sleepRandomTime(500, 1000);
            }
            if (orderId == null){
                LOGGER.warn("queryOrderWaitTime获取订单ID失败");
                return null;
            }
            //检查订单提交是否成功
            success = api12306Service.resultOrderForDcQueue(session, orderId, object.getString("token"));
            if (!success) {
                LOGGER.warn("resultOrderForDcQueue检查订单没有成功");
                return null;
            }
            return orderId;
        } catch (UnfinishedOrderException e) {
            LOGGER.warn(e.getMessage());
            return null;
        }
    }



    /**
     *
     * @param openID 用户的微信openID
     * @param secretStr 车次的密钥（当前只限定一种车次，12306规则是一个候补订单可以选择两种【车次+座位】的组合）
     * @param seatTypes 选择的座位类型，座位类型数量和乘客数量一致
     * @param passengerNames 选择的乘客，每个12306账户最多只能有一个候补订单，候补订单最多可以有3个乘客
     * @return 是否成功
     */
    public boolean submitByAfterNate(String openID,
                                     String secretStr,
                                     List<String> seatTypes,
                                     List<String> passengerNames) {

        //有不订单一次最多只能有3个乘客
        while (passengerNames.size() > 3){
            passengerNames.remove(passengerNames.size()-1);
            seatTypes.remove(seatTypes.size()-1);
            LOGGER.warn("用户数量大于3，移除后面的用户");
        }
        WxAccount wxAccount = wxAccountRepository.findByOpenId(openID);
        Session session = sessionPoolService.getSession(wxAccount.getUsername());
        try {
            //检查用户是否登陆成功
            //检查用户是否登陆成功
            boolean success = api12306Service.checkLogin(session);
            if (!success) {
                login12306Service.login(wxAccount.getUsername(), wxAccount.getPassword());
                priorityService.sleepRandomTime(1000, 2000);
                success = api12306Service.checkLogin(session);
                if (!success){
                    LOGGER.error("submitByAfterNate：登陆失败，提交订单失败");
                    return false;
                }
            }
            priorityService.sleepRandomTime(500, 1000);
            success = api12306Service.checkFace(session, secretStr, seatTypes.get(0));
            if (!success) {
                LOGGER.warn("checkFace失败");
                return false;
            }
            priorityService.sleepRandomTime(500, 1000);
            JSONObject res = api12306Service.getSuccessRate(session, secretStr, seatTypes.get(0));
            if (!res.getBoolean("status")) {
                LOGGER.warn("submitANOrderRequest失败");
                return false;
            }
            priorityService.sleepRandomTime(500, 1000);
            res = res.getJSONObject("data").getJSONArray("flag").getJSONObject(0);
            String trainNo = res.getString("train_no");
            String count = res.getString("info");//候补人数
            LOGGER.info(String.format("提交候补订单，当前候补人数：【%s】", count));
            success = api12306Service.submitANOrderRequest(session, secretStr, seatTypes.get(0));
            if (!success) {
                LOGGER.warn("submitANOrderRequest失败");
                return false;
            }
            priorityService.sleepRandomTime(500, 1000);
            //获取联系人
            res = api12306Service.getTokenAndTicket(session);
            List<Passenger> passengerList = api12306Service.getPassengersWithToken(session, res.getString("token"));
            if (passengerList == null) {
                LOGGER.warn("getPassengersWithToken失败");
                return false;
            }
            List<Passenger> passengers = findPassengers(passengerList, passengerNames);
            if (passengers.isEmpty()) {
                LOGGER.warn("找不到该乘客");
                return false;
            }

            priorityService.sleepRandomTime(500, 1000);
            res = api12306Service.initPassengerApi(session);
            if (!res.getBoolean("status")) {
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
            for (Passenger passenger : passengers){
                passengerInfo.append(String.format("1#%s#1#%s#%s;", passenger.getPassenger_name(),
                        passenger.getPassenger_id_no(), passenger.getAllEncStr()));
            }
            priorityService.sleepRandomTime(500, 1000);
            success = api12306Service.confirmHB(session, passengerInfo.toString(), jzParam, trainNo, seatTypes.get(0));
            if (!success) {
                LOGGER.warn("confirmHB失败");
                return false;
            }
            //候补排队
            priorityService.sleepRandomTime(500, 1000);
            success = api12306Service.queryANQueue(session);
            if (!success) {
                LOGGER.warn("queryANQueue失败");
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e);
            return false;
        }
        return true;
    }
}
