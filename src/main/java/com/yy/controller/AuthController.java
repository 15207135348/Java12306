package com.yy.controller;

import com.yy.common.log.RecordLog;
import com.yy.integration.rail.PersonalCenter;
import com.yy.dao.repository.PassengerRepository;
import com.yy.dao.repository.WxAccountRepository;
import com.yy.dao.entity.Passenger;
import com.yy.dao.entity.WxAccount;
import com.yy.other.domain.RespMessage;
import com.yy.integration.rail.Login12306;
import com.yy.service.authority.CheckPermission;
import com.yy.service.authority.CookieManager;
import com.yy.service.authority.LoginWXService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 登陆
 *
 * @author yy
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    private static final Logger LOGGER = Logger.getLogger(AuthController.class);


    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    private WxAccountRepository wxAccountRepository;

    @Autowired
    private LoginWXService loginWXService;

    @RecordLog
    @GetMapping(value = "/login_wx")
    @ResponseBody
    public RespMessage loginWX(HttpServletRequest request, HttpServletResponse response) {
        String code = request.getParameter("code");
        if (code == null) {
            return new RespMessage(false, "code为空");
        }
        try {
            if (!loginWXService.login(code)){
                return new RespMessage(false, "code2Session出现错误");
            }
            //返回消息
            return new RespMessage(true, "登陆成功");
        } catch (Exception e) {
            LOGGER.error("loginWX:服务器内部错误");
            return new RespMessage(false, "服务器内部错误");
        }
    }

    /**
     * 获取12306的用户名
     *
     * @param request
     * @param response
     * @return
     */
    @CheckPermission
    @RecordLog
    @GetMapping(value = "/get_12306_account")
    @ResponseBody
    public RespMessage get12306Account(HttpServletRequest request, HttpServletResponse response) {
        RespMessage respMessage = new RespMessage();
        String openID = CookieManager.getOpenIDFromRequest(request);
        if (openID == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请先调用login_wx接口进行登陆");
            return respMessage;
        }
        WxAccount wxAccount = wxAccountRepository.findByOpenId(openID);
        if (wxAccount == null || wxAccount.getUsername() == null || wxAccount.getPassword() == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("请求设置12306账户");
            return respMessage;
        }
        respMessage.setSuccess(true);
        respMessage.setMessage(wxAccount.getUsername());
        return respMessage;
    }

    /**
     * 获取12306的用户名
     *
     * @param request
     * @param response
     * @return
     */
    @CheckPermission
    @RecordLog
    @GetMapping(value = "/set_12306_account")
    @ResponseBody
    public RespMessage set12306Account(HttpServletRequest request, HttpServletResponse response) {
        RespMessage respMessage = new RespMessage();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || password == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("用户名或者密码不能为空");
            return respMessage;
        }
        //验证12306账户密码是否正确
        try {
            RespMessage loginRes = Login12306.login(username, password);
            if (!loginRes.isSuccess()) {
                return loginRes;
            }
        } catch (Exception e) {
            respMessage.setSuccess(false);
            respMessage.setMessage("服务器内部错误");
            return respMessage;
        }
        //设置用户名和密码
        String openID = CookieManager.getOpenIDFromRequest(request);
        WxAccount wxAccount = wxAccountRepository.findByOpenId(openID);
        wxAccount.setUsername(username);
        wxAccount.setPassword(password);
        wxAccountRepository.save(wxAccount);
        //获取乘客人信息，并保存到数据库
        new Thread(() -> {
            List<Passenger> passengers = PersonalCenter.getPassengers(username);
            for (Passenger passenger : passengers) {
                Passenger passenger1 = passengerRepository.findByUsernameAndName(passenger.getUsername(), passenger.getName());
                if (passenger1 == null) {
                    passengerRepository.save(passenger);
                    LOGGER.info("添加乘客人");
                } else {
                    passenger.setId(passenger1.getId());
                    passengerRepository.save(passenger);
                    LOGGER.info("更新乘客人");
                }
            }
        }).start();
        respMessage.setSuccess(true);
        respMessage.setMessage("操作成功");
        return respMessage;
    }
}
