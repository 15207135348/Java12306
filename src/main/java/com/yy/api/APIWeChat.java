package com.yy.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yy.dao.entity.WxAccount;
import com.yy.util.HttpClient;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import static com.yy.config.WeChatConfig.*;

import java.util.List;


public class APIWeChat {

    private static final Logger LOGGER = Logger.getLogger(APIWeChat.class);


    public static WxAccount code2Session(String code) throws Exception {
        Object [] params = new Object[]{"appid", "secret", "js_code", "grant_type"};
        Object [] values = new Object[]{appID, appSecret, code, "authorization_code"};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        JSONObject obj = (JSONObject) HttpClient.sendGet(CODE2SESSION_URL, paramsList);
        WxAccount wxAccount = null;
        if (obj != null && obj.containsKey("openid") && obj.containsKey("session_key")) {
            wxAccount = new WxAccount();
            wxAccount.setOpenId(obj.getString("openid"));
            wxAccount.setSessionKey(obj.getString("session_key"));
            if (obj.containsKey("unionid")) {
                wxAccount.setUnionId(obj.getString("unionid"));
            }
        }
        return wxAccount;
    }

    private static String getAccessToken(){
        //获取微信token
        Object [] params = new Object[]{"grant_type", "appid", "secret"};
        Object [] values = new Object[]{"client_credential", appID, appSecret};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        JSONObject result = null;
        try {
            result = (JSONObject) HttpClient.sendGet(GER_ACCESS_TOKEN_URL, paramsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            return null;
        }
        return result.getString("access_token");
    }

    public static boolean sendCommonTicket(String openID, String jumpPage, JSONObject data) {
        return send(openID, jumpPage, data, templateCommonTicket);
    }

    public static boolean sendAlternateTicket(String openID, String jumpPage, JSONObject data) {
        return send(openID, jumpPage, data, templateAlternateTicket);
    }

    private static boolean send(String openID, String jumpPage, JSONObject data, String templateAlternateTicket) {
        String accessToken = getAccessToken();
        if (accessToken == null){
            return false;
        }
        String url = String.format(SEND_MSG_URL, accessToken);
        JSONObject params = new JSONObject();
        params.put("touser", openID);
        params.put("template_id", templateAlternateTicket);
        params.put("page", jumpPage);
        params.put("data", data);
        try {
            JSONObject res = JSON.parseObject(HttpClient.postJson(url, params));
            LOGGER.info(res);
            if (res.getInteger("errcode") == 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
