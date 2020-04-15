package com.yy.controller;

import com.yy.aop.interfaces.RecordLog;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Controller
@RequestMapping("/wx")
public class WXController {


    @Value("${app.token}")
    private String TOKEN;

    @RecordLog
    @GetMapping(value = "/on_message")
    @ResponseBody
    public String onMessage(HttpServletRequest request, HttpServletResponse response)
    {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echostr = request.getParameter("echostr");
        String[] tmpArr = new String[]{TOKEN, timestamp, nonce};
        Arrays.sort(tmpArr);
        String tmpStr = tmpArr[0] + tmpArr[1] + tmpArr[2];
        tmpStr = DigestUtils.sha1Hex(tmpStr);
        if (tmpStr.equals(signature)) {
            return echostr;
        } else {
            return echostr;
        }
    }
}
