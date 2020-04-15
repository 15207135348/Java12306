package com.yy.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yy.aop.interfaces.RecordLog;
import com.yy.constant.UserOrderStatus;
import com.yy.dao.*;
import com.yy.dao.entity.*;
import com.yy.domain.RespMessage;
import com.yy.domain.Train;
import com.yy.service.api.APIWXService;
import com.yy.service.core.QueryTrainService;
import com.yy.service.core.UserOrderPoolService;
import com.yy.service.util.CookieService;
import com.yy.service.util.SessionPoolService;
import com.yy.util.TimeFormatUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/12306")
public class Fuck12306Controller {

    private static final Logger LOGGER = Logger.getLogger(Fuck12306Controller.class);
    @Autowired
    APIWXService apiwxService;
    @Autowired
    private WxAccountRepository wxAccountRepository;
    @Autowired
    private UserOrderRepository userOrderRepository;
    @Autowired
    private TrainOrderRepository trainOrderRepository;
    @Autowired
    private TrainAnOrderRepository trainAnOrderRepository;
    @Autowired
    private QueryTrainService queryTicketService;
    @Autowired
    private UserOrderPoolService orderPoolService;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private CookieService cookieService;
    @Autowired
    private TrainTicketRepository trainTicketRepository;
    @Autowired
    private SessionPoolService sessionPoolService;

    @RecordLog
    @GetMapping(value = "/get_trains")
    @ResponseBody
    public RespMessage getTrains(HttpServletRequest request, HttpServletResponse response) {
        RespMessage respMessage = new RespMessage();
        String fromStation = request.getParameter("fromStation");
        String toStation = request.getParameter("toStation");
        String dates = request.getParameter("dates");
        String[] dateArray = TimeFormatUtil.CNTime2UNTime(dates).split("/");
        Arrays.sort(dateArray, Comparator.reverseOrder());
        try {
            List<Train> trains = queryTicketService.getTrains(sessionPoolService.getSession(null),
                dateArray[0], fromStation, toStation);
            if (trains == null)
            {
                sessionPoolService.remove(null);
                trains = queryTicketService.getTrains(sessionPoolService.getSession(null),
                    dateArray[0], fromStation, toStation);
            }
//            queryTicketService.setPrices(trains);
            JSONArray array = new JSONArray();
            for (Train train : trains) {
                array.add(train.toJSON());
            }
            respMessage.setSuccess(true);
            respMessage.setMessage(array.toJSONString());
        } catch (Exception e) {
            LOGGER.error("系统内部错误:" + e.getMessage());
            respMessage.setSuccess(false);
            respMessage.setMessage("系统内部错误:" + e.getMessage());
        }
        return respMessage;
    }

    @RecordLog
    @GetMapping(value = "/get_people")
    @ResponseBody
    public RespMessage getPeople(HttpServletRequest request, HttpServletResponse response) {
        RespMessage respMessage = new RespMessage();
        String openID = cookieService.getOpenIDFromRequest(request);
        if (openID == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请先调用login_wx接口进行登陆");
            return respMessage;
        }
        WxAccount wxAccount = wxAccountRepository.findByOpenId(openID);
        if (wxAccount == null || wxAccount.getUsername() == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请先登陆12306账户");
            return respMessage;
        }
        List<Passenger> passengers = passengerRepository.findAllByUsername(wxAccount.getUsername());
        JSONArray array = new JSONArray();
        for (Passenger passenger : passengers) {
            array.add(passenger.toJSON());
        }
        respMessage.setSuccess(true);
        respMessage.setMessage(array.toJSONString());
        return respMessage;
    }

    @RecordLog
    @GetMapping(value = "/get_orders")
    @ResponseBody
    public RespMessage getOrders(HttpServletRequest request, HttpServletResponse response) {

        RespMessage respMessage = new RespMessage();
        String openID = cookieService.getOpenIDFromRequest(request);
        if (openID == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请先调用login_wx接口进行登陆");
            return respMessage;
        }
        JSONArray array = new JSONArray();
        List<UserOrder> orders = userOrderRepository.findAllByOpenId(openID);
        if (orders == null || orders.isEmpty()) {
            respMessage.setSuccess(false);
            respMessage.setMessage(array.toJSONString());
            return respMessage;
        }
        for (UserOrder order : orders) {
            JSONObject object = (JSONObject) order.toJSON();
            if (orderPoolService.isInBreakTime() &&
                (object.getString("status").equals(UserOrderStatus.RUSHING.getStatus()) ||
                    object.getString("status").equals(UserOrderStatus.AN_RUSHING.getStatus()))) {
                object.put("status", UserOrderStatus.SLEEPING.getStatus());
                LOGGER.info("当前处于休息时间，返回给前端的状态修改为休息中");
            }
            array.add(object);
        }
        respMessage.setSuccess(true);
        respMessage.setMessage(array.toJSONString());
        return respMessage;
    }

    @RecordLog
    @GetMapping(value = "/get_query_count")
    @ResponseBody
    public RespMessage getQueryCount(HttpServletRequest request, HttpServletResponse response) {

        RespMessage respMessage = new RespMessage();
        String orderId = request.getParameter("orderId");
        if (orderId == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("参数错误");
            return respMessage;
        }
        int count = userOrderRepository.findQueryCountByOrderId(orderId);
        respMessage.setSuccess(true);
        respMessage.setMessage(String.valueOf(count));
        return respMessage;
    }

    @RecordLog
    @GetMapping(value = "/cancel_order")
    @ResponseBody
    public RespMessage cancelOrder(HttpServletRequest request, HttpServletResponse response) {

        RespMessage respMessage = new RespMessage();
        String orderId = request.getParameter("orderId");
        if (orderId == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("参数错误");
            return respMessage;
        }
        UserOrder order = userOrderRepository.findByOrderId(orderId);
        if (order == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("找不到订单");
            return respMessage;
        }
        orderPoolService.cancelOrder(order);
        respMessage.setSuccess(true);
        respMessage.setMessage("订单已取消");
        return respMessage;
    }

    @RecordLog
    @GetMapping(value = "/delete_order")
    @ResponseBody
    public RespMessage deleteOrder(HttpServletRequest request, HttpServletResponse response) {

        RespMessage respMessage = new RespMessage();
        String orderId = request.getParameter("orderId");
        if (orderId == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("参数错误");
            return respMessage;
        }
        UserOrder userOrder = userOrderRepository.findByOrderId(orderId);
        if (userOrder.getRushType().equals("候补抢票")) {
            trainAnOrderRepository.deleteByUserOrderId(orderId);
        } else {
            trainOrderRepository.deleteByUserOrderId(orderId);
            trainTicketRepository.deleteAllByUserOrderId(orderId);
        }
        userOrderRepository.deleteByOrderId(orderId);
        respMessage.setSuccess(true);
        respMessage.setMessage("订单已删除");
        return respMessage;
    }

    /**
     * 用户提交订单
     *
     * @param request
     * @param response
     * @return
     */
    @RecordLog
    @GetMapping(value = "/fuck12306")
    @ResponseBody
    public RespMessage submitOrder(HttpServletRequest request, HttpServletResponse response) {
        RespMessage respMessage = new RespMessage();
        String openID = cookieService.getOpenIDFromRequest(request);
        if (openID == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请先调用login_wx接口进行登陆");
            return respMessage;
        }
        WxAccount wxAccount = wxAccountRepository.findByOpenId(openID);
        if (wxAccount.getUsername() == null || wxAccount.getPassword() == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请先绑定12306账号");
            return respMessage;
        }
        String people = request.getParameter("people");
        String contactInfo = request.getParameter("contactInfo");
        String fromStation = request.getParameter("fromStation");
        String toStation = request.getParameter("toStation");
        String dates = TimeFormatUtil.CNTime2UNTime(request.getParameter("dates"));
        String trains = request.getParameter("trains");
        String seats = request.getParameter("seats");
        String rushTypes = request.getParameter("rushTypes");
        //下单时间
        Timestamp orderTime = new Timestamp(System.currentTimeMillis());
        //订单过期时间
        Timestamp expireTime = new Timestamp(queryTicketService.getExpireTime(sessionPoolService.getSession(null),
            dates, fromStation, toStation, trains));

        String[] strings = rushTypes.split("/");
        for (String rushType : strings) {
            String orderId = UUID.randomUUID().toString().replaceAll("-", "");
            String status = rushType.equals("实时抢票") ? UserOrderStatus.RUSHING.getStatus() : UserOrderStatus.AN_RUSHING.getStatus();
            UserOrder order = new UserOrder(orderId, fromStation, toStation, dates, trains, seats, people,
                contactInfo, rushType, 0, orderTime, expireTime, status, 0, openID);
            //保存用户提交的订单
            userOrderRepository.save(order);
            //将订单加入订单池，由订单池负责处理订单
            orderPoolService.addOrder(order);
        }
        respMessage.setSuccess(true);
        respMessage.setMessage("正在为您抢票");
        return respMessage;
    }

    @RecordLog
    @GetMapping(value = "/get_success_info")
    @ResponseBody
    public RespMessage getSuccessInfo(HttpServletRequest request, HttpServletResponse response) {

        RespMessage respMessage = new RespMessage();
        String orderId = request.getParameter("orderId");
        if (orderId == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("参数错误");
            return respMessage;
        }
        //火车票订单详情
        TrainOrder order = trainOrderRepository.findByUserOrderId(orderId);
        JSONObject res = new JSONObject();
        res.put("sequenceNo", order.getSequenceNo());
        res.put("status", order.getStatus());
        String[] t1 = TimeFormatUtil.stamp2Date(order.getFromTime().getTime(), "MM月dd日 HH:mm").split(" ");
        String[] t2 = TimeFormatUtil.stamp2Date(order.getToTime().getTime(), "MM月dd日 HH:mm").split(" ");
        res.put("fromDate", t1[0]);
        res.put("fromTime", t1[1]);
        res.put("toDate", t2[0]);
        res.put("toTime", t2[1]);
        res.put("fromStation", order.getFromStation());
        res.put("toStation", order.getToStation());
        res.put("duration", order.getDuration());
        res.put("trainCode", order.getTrainCode());
        //火车票订单中的所有火车票的详情
        List<TrainTicket> tickets = trainTicketRepository.findAllByUserOrderId(orderId);
        JSONArray array = new JSONArray();
        for (TrainTicket ticket : tickets) {
            array.add(ticket.toJSON());
        }
        res.put("tickets", array);
        respMessage.setSuccess(true);
        respMessage.setMessage(res.toJSONString());
        return respMessage;
    }

    @RecordLog
    @GetMapping(value = "/get_an_success_info")
    @ResponseBody
    public RespMessage getAnSuccessInfo(HttpServletRequest request, HttpServletResponse response) {

        RespMessage respMessage = new RespMessage();
        String orderId = request.getParameter("orderId");
        if (orderId == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("参数错误");
            return respMessage;
        }
        JSONObject res = new JSONObject();
        //火车票订单详情
        TrainAnOrder trainAnOrder = trainAnOrderRepository.findByUserOrderId(orderId);
        res.put("reserveNo", trainAnOrder.getReserveNo());
        res.put("status", trainAnOrder.getStatus());
        String[] t1 = TimeFormatUtil.stamp2Date(trainAnOrder.getFromTime().getTime(), "MM月dd日 HH:mm").split(" ");
        String[] t2 = TimeFormatUtil.stamp2Date(trainAnOrder.getToTime().getTime(), "MM月dd日 HH:mm").split(" ");
        res.put("fromDate", t1[0]);
        res.put("fromTime", t1[1]);
        res.put("toDate", t2[0]);
        res.put("toTime", t2[1]);
        res.put("fromStation", trainAnOrder.getFromStation());
        res.put("toStation", trainAnOrder.getToStation());
        res.put("duration", trainAnOrder.getDuration());
        res.put("trainCode", trainAnOrder.getTrainCode());
        res.put("prepayAmount", trainAnOrder.getPrepayAmount());
        res.put("seatType", trainAnOrder.getSeatType());
        res.put("queueInfo", trainAnOrder.getQueueInfo());
        //火车票订单中的所有火车票的详情
        JSONArray passages = JSONArray.parseArray(trainAnOrder.getPassengers());
        res.put("passages", passages);
        respMessage.setSuccess(true);
        respMessage.setMessage(res.toJSONString());
        return respMessage;
    }


}
