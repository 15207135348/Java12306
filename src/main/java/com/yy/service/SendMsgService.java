package com.yy.service;

import com.alibaba.fastjson.JSONObject;
import com.yy.dao.entity.UserOrder;
import com.yy.api.APIWeChat;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendMsgService {

    private static final Logger LOGGER = Logger.getLogger(SendMsgService.class);

    @Autowired
    EmailService emailService;


    public void send(UserOrder order, String ticketNo) {
        try {
            //通过微信下发通知
            JSONObject data = new JSONObject();
            JSONObject j1 = new JSONObject();
            JSONObject j2 = new JSONObject();
            JSONObject j3 = new JSONObject();
            j1.put("value", "抢票成功");
            j2.put("value", ticketNo);
            j3.put("value", "恭喜您抢票成功！请在30分钟内完成支付");
            data.put("phrase2", j1);
            data.put("character_string1", j2);
            data.put("thing4", j3);
            boolean res = APIWeChat.sendCommonTicket(order.getOpenId(), "/pages/order/order", data);
            LOGGER.info("微信消息通知结果" + res);
            //通过手机号或者邮箱通知用户
            if (order.getContactInfo().contains("@")) {
                emailService.sendTextEmail(order.getContactInfo(), "抢票结果",
                        String.format("恭喜您抢票成功！您的取票号为：【%s】，请在30分钟内前去12306完成支付\r\n", ticketNo) +
                                "https://www.12306.cn/index");
            } else {
                LOGGER.warn("暂时还不支持手机号通知");
            }
        }catch (Exception e){
            LOGGER.error("send:" + e.getMessage());
        }
    }

    public void sendAlternate(UserOrder order) {
        try {
            //通过微信下发通知
            JSONObject data = new JSONObject();
            JSONObject j1 = new JSONObject();
            JSONObject j2 = new JSONObject();
            j1.put("value", "候补成功");
            j2.put("value", "恭喜您候补成功！请在30分钟内完成支付");
            data.put("phrase2", j1);
            data.put("thing4", j2);
            boolean res = APIWeChat.sendAlternateTicket(order.getOpenId(), "/pages/order/order", data);
            LOGGER.info("微信消息通知结果" + res);
            //通过手机号或者邮箱通知用户
            if (order.getContactInfo().contains("@")) {
                emailService.sendTextEmail(order.getContactInfo(), "抢票结果", "恭喜您抢到一张候补车票！请在30分钟内前去12306完成支付\r\n" +
                        "另外，为了防止12306候补车票兑现失败，我们会在您候补车票兑现前继续为您抢其它车次或者同一车次其它座位类型的车票！" +
                        "https://www.12306.cn/index");
            } else {
                LOGGER.warn("暂时还不支持手机号通知");
            }
        }catch (Exception e){
            LOGGER.error("sendAlternate:" + e.getMessage());
        }
    }
}
