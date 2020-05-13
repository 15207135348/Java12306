package com.yy.api.rail;

import com.yy.api.API12306;
import com.yy.api.APIPython;
import com.yy.domain.RespMessage;
import com.yy.domain.Session;
import com.yy.factory.SessionFactory;
import org.apache.log4j.Logger;

public class Login12306 {

    private static final Logger LOGGER = Logger.getLogger(Login12306.class);

    public static RespMessage login(String username, String password) {
        /**
         * 登陆步骤：
         * 1. 获取校验码图片
         * 2. 识别图片获取校验码答案
         * 3. 验证校验码答案是否正确
         * 4. 通过用户、密码和校验码答案登陆
         * 5. 验证是否登陆成功
         * 6. 获取用户的用户名
         */
        Session session = SessionFactory.getSession(username);
        RespMessage respMessage = new RespMessage();
        //1获取校验码
        String base64Image = API12306.getBase64Image(session);
        if (base64Image == null) {
            SessionFactory.remove(username);
            session = SessionFactory.getSession(username);
            base64Image = API12306.getBase64Image(session);
            if (base64Image == null) {
                respMessage.setSuccess(false);
                respMessage.setMessage("验证码下载失败");
                return respMessage;
            }
        }
        //2获取答案
        String answer = APIPython.getAnswer(base64Image);
        if (answer == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("校验码识别异常，检查校验码识别服务是否开启");
            return respMessage;
        }
        //3检验答案是否正确
        while (!API12306.checkAnswer(session, answer)) {
            //如果检测结果错误，重新下载校验码并校验
            base64Image = API12306.getBase64Image(session);
            if (base64Image == null) {
                respMessage.setSuccess(false);
                respMessage.setMessage("验证码下载失败");
                return respMessage;
            }
            answer = APIPython.getAnswer(base64Image);
            if (answer == null) {
                respMessage.setSuccess(false);
                respMessage.setMessage("校验码识别异常，检查校验码识别服务是否开启");
                return respMessage;
            }
        }
        //4/5/6 校验成功，登陆用户名和密码，并检查是否登陆成功
        String nickname = API12306.baseLogin(session, username, password, answer);
        if (nickname == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("用户名或者密码错误");
            return respMessage;
        }
        respMessage.setSuccess(true);
        respMessage.setMessage(nickname);
        return respMessage;
    }

    //定时检查用户是否登陆成功，如果不成功，则自动重新登陆
    public static void checkUser(String username, String password) {
        Session session = SessionFactory.getSession(username);
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    if (!API12306.checkLogin(session)) {
                        login(username, password);
                    }
                    Thread.sleep(6000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
