package com.yy.service.core;

import com.yy.domain.RespMessage;
import com.yy.domain.Session;
import com.yy.service.api.API12306Service;
import com.yy.service.api.APIPythonService;
import com.yy.service.util.SessionPoolService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Login12306Service {

    private static final Logger LOGGER = Logger.getLogger(Login12306Service.class);

    @Autowired
    SessionPoolService sessionPoolService;
    @Autowired
    API12306Service api12306Service;
    @Autowired
    APIPythonService apiPythonService;

    public RespMessage login(String username, String password) {
        /**
         * 登陆步骤：
         * 1. 获取校验码图片
         * 2. 识别图片获取校验码答案
         * 3. 验证校验码答案是否正确
         * 4. 通过用户、密码和校验码答案登陆
         * 5. 验证是否登陆成功
         * 6. 获取用户的用户名
         */
        Session session = sessionPoolService.getSession(username);
        RespMessage respMessage = new RespMessage();
        //1获取校验码
        String base64Image = api12306Service.getBase64Image(session);
        if (base64Image == null) {
            sessionPoolService.remove(username);
            session = sessionPoolService.getSession(username);
            base64Image = api12306Service.getBase64Image(session);
            if (base64Image == null)
            {
                respMessage.setSuccess(false);
                respMessage.setMessage("验证码下载失败");
                return respMessage;
            }
        }
        //2获取答案
        String answer = apiPythonService.getAnswer(base64Image);
        if (answer == null) {
            respMessage.setSuccess(false);
            respMessage.setMessage("校验码识别异常，检查校验码识别服务是否开启");
            return respMessage;
        }
        //3检验答案是否正确
        while (!api12306Service.checkAnswer(session, answer)) {
            //如果检测结果错误，重新下载校验码并校验
            base64Image = api12306Service.getBase64Image(session);
            if (base64Image == null) {
                respMessage.setSuccess(false);
                respMessage.setMessage("验证码下载失败");
                return respMessage;
            }
            answer = apiPythonService.getAnswer(base64Image);
            if (answer == null) {
                respMessage.setSuccess(false);
                respMessage.setMessage("校验码识别异常，检查校验码识别服务是否开启");
                return respMessage;
            }
        }
        //4/5/6 校验成功，登陆用户名和密码，并检查是否登陆成功
        String nickname = api12306Service.baseLogin(session, username, password, answer);
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
    public void checkUser(String username, String password) {
        Session session = sessionPoolService.getSession(username);
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    if (!api12306Service.checkLogin(session)) {
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

//    @Autowired
//    WxAccountRepository wxAccountRepository;
//
//    @PostConstruct
//    public void test() {
//        List<WxAccount> list = wxAccountRepository.findAll();
//        list.sort((o1, o2) -> o2.getId() - o1.getId());
//        int count = 0;
//        for (int i = 0; i < 4; ++i){
//            for (WxAccount wxAccount : list){
//                if (wxAccount.getUsername() == null || wxAccount.getPassword() == null){
//                    continue;
//                }
//                login(wxAccount.getUsername(), wxAccount.getPassword());
//                LOGGER.info(String.format("当前登陆次数【%d】", ++count));
//            }
//        }
//    }
}
