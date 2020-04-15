package com.yy.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yy.dao.entity.WxAccount;
import com.yy.util.HttpClient;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class APIWXService {

    private static final Logger LOGGER = Logger.getLogger(APIWXService.class);

    @Value("${app.id}")
    private String appID;
    @Value("${app.secret}")
    private String appSecret;
    @Value("${app.CODE2SESSION_URL}")
    private String CODE2SESSION_URL;

    @Value("${app.GER_ACCESS_TOKEN_URL}")
    private String GER_ACCESS_TOKEN_URL;
    @Value("${app.SEND_MSG_URL}")
    private String SEND_MSG_URL;
    @Value("${app.template_common_ticket}")
    private String templateCommonTicket;
    @Value("${app.template_alternate_ticket}")
    private String templateAlternateTicket;

    public WxAccount code2Session(String code) throws Exception {
        Object [] params = new Object[]{"appid", "secret", "js_code", "grant_type"};
        Object [] values = new Object[]{appID, appSecret, code, "authorization_code"};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        JSONObject obj = (JSONObject) HttpClient.sendGet(CODE2SESSION_URL, paramsList);
        /**
         * 属性	类型	说明
         * openid	string	用户唯一标识
         * session_key string	会话密钥
         * unionid	string	用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回，详见 UnionID 机制说明。
         * errcode	number	错误码
         * errmsg	string	错误信息
         */
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

    private String getAccessToken(){
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

    public boolean sendCommonTicket(String openID, String jumpPage, JSONObject data) {
        String accessToken = getAccessToken();
        if (accessToken == null){
            return false;
        }
        String url = String.format(SEND_MSG_URL, accessToken);
        JSONObject params = new JSONObject();
        params.put("touser", openID);
        params.put("template_id", templateCommonTicket);
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

    public boolean sendAlternateTicket(String openID, String jumpPage, JSONObject data) {
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
