package com.yy.integration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yy.other.constant.SeatType;
import com.yy.other.domain.JSFunction;
import com.yy.other.domain.Passenger;
import com.yy.other.domain.HttpSession;
import com.yy.other.exception.UnfinishedOrderException;
import com.yy.other.domain.HttpClient;
import com.yy.other.domain.JSParser;
import com.yy.common.util.SleepUtil;
import com.yy.common.util.TimeFormatUtil;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yy.other.config.RailWayConfig.*;

public class API12306 {
    private static final Logger LOGGER = Logger.getLogger(API12306.class);


    private static Map<String, String> hashAlg(String script) {
        StringBuilder builder = new StringBuilder();
        Map<String, String> params = new HashMap<>();
        Map<String, String> data = AlgHelper.getData();
        for (String key : data.keySet()) {
            String value = data.get(key);
            builder.append(key).append(value);
            String key2 = AlgHelper.table.getOrDefault(key, key);
            params.put(key2, value);
        }
        String dataStr = builder.toString();
        String hashCode = AlgHelper.getHashCode(script, dataStr);
        params.put("hashCode", hashCode);
        return params;
    }

    public static Map<String, String> getDeviceID(HttpSession session) {
        String algID = null;
        Object o = session.httpsGet(GET_JS_URL, null);
        if (o instanceof String) {
            String content = (String) o;
            Pattern pattern = Pattern.compile("algID\\\\x3d(.*?)\\\\x26");
            Matcher m = pattern.matcher(content);
            if (m.find()) {
                algID = m.group(1);
            }
        }
        if (algID == null) {
            return null;
        }
        long timestamp = System.currentTimeMillis();
        //核心，计算参数
        Map<String, String> map = hashAlg((String) o);
        Object[] params = new Object[map.size() + 2];
        Object[] values = new Object[map.size() + 2];
        int i = 0;
        for (String key : map.keySet()) {
            params[i] = key;
            values[i++] = map.get(key);
        }
        params[i] = "algID";
        values[i++] = algID;
        params[i] = "timestamp";
        values[i] = timestamp;
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        o = session.httpsPost(LOG_DEVICE_URL, paramsList, null);
        JSONObject result = null;
        if (o instanceof String) {
            String content = (String) o;
            if (content.contains("callbackFunction")) {
                content = content.substring(18, content.length() - 2);
                result = JSONObject.parseObject(content);
            }
        }
        if (result == null) {
            return null;
        }
        Map<String, String> map1 = new HashMap<>();
        map1.put("RAIL_DEVICEID", result.getString("dfp"));
        map1.put("RAIL_EXPIRATION", result.getString("exp"));
        return map1;
    }


    public static String getStations(HttpSession session) {
        Object o = session.httpsGet(GET_STATIONS_URL, null);
        if (!(o instanceof String)) {
            return null;
        }
        String res = (String) o;
        return res.replace("var station_names ='@", "");
    }


    /**
     * 获取校验码图片的BASE64字符串
     *
     * @param session
     * @return
     */
    public static String getBase64Image(HttpSession session) {
        //第1步 获取校验码图片
        String url = String.format(IMAGE_URL, System.currentTimeMillis());
        Map<String, String> head = new HashMap<>();
        head.put("Host", "kyfw.12306.cn");
        head.put("Connection", "keep-alive");
        head.put("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
        head.put("Referer", "https://kyfw.12306.cn/otn/resources/login.html");
        Object o = session.httpsGet(url, head);
        if (!(o instanceof String)) {
            return null;
        }
        String res = (String) o;
        String json = res.substring(res.indexOf("(") + 1, res.lastIndexOf(")"));
        JSONObject obj = null;
        try {
            obj = JSON.parseObject(json);
        } catch (Exception e) {
            LOGGER.error("getBase64Image: obj = JSON.parseObject(json)报异常");
            return null;
        }
        if (obj == null || obj.getInteger("result_code") != 0) {
            return null;
        }
        return obj.getString("image");
    }

    /**
     * 校验答案是否正确
     *
     * @param session
     * @param answer
     * @return
     */
    public static boolean checkAnswer(HttpSession session, String answer) {
        long timestamp = System.currentTimeMillis();
        String url = String.format(CHECK_ANSWER_URL, answer, timestamp);
        String checkRes = (String) session.httpsGet(url, null);
        JSONObject json = JSON.parseObject(checkRes.substring(checkRes.indexOf("(") + 1, checkRes.lastIndexOf(")")));
        return json.getString("result_code").equals("4");
    }

    /**
     * @param session
     * @param username 用户名
     * @param password 密码
     * @param answer   校验码答案
     * @return 登陆的用户昵称
     */
    public static String baseLogin(HttpSession session, String username, String password, String answer) {
        Object[] params = new Object[]{"username", "password", "appid", "answer"};
        Object[] values = new Object[]{username, password, "otn", answer};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(LOGIN_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.warn("请求出错" + LOGIN_URL);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (res.getInteger("result_code") != 0) {
            LOGGER.warn(res.getString("result_message"));
            return null;
        }
        //验证是否登陆成功
        params = new Object[]{"appid"};
        values = new Object[]{"otn"};
        paramsList = HttpClient.getParams(params, values);
        Map<String, String> head = new LinkedHashMap<>();
        head.put("Origin", "https://kyfw.12306.cn");
        head.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        head.put("Referer", "https://kyfw.12306.cn/otn/passport?redirect=/otn/login/userLogin");
        o = session.httpsPost(UAMTK_URL, paramsList, head);
        if (!(o instanceof JSONObject)) {
            LOGGER.warn("请求出错：" + UAMTK_URL);
            return null;
        }
        res = (JSONObject) o;
        if (res.getInteger("result_code") != 0) {
            LOGGER.warn(res.getString("result_message"));
            return null;
        }
        String tk = res.getString("newapptk");
        //获取用户的用户名
        params = new Object[]{"tk"};
        values = new Object[]{tk};
        paramsList = HttpClient.getParams(params, values);
        o = session.httpsPost(UAMAUTHCLIENT_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.warn("请求出错" + UAMAUTHCLIENT_URL);
            return null;
        }
        res = (JSONObject) o;
        if (res.getInteger("result_code") != 0) {
            LOGGER.warn(res.getString("result_message"));
            return null;
        }
        LOGGER.info(String.format("%s登陆成功", res.getString("username")));
        return res.getString("username");
    }

    /**
     * 检查用户是否处于登陆状态，如果没有，需要重新登陆
     *
     * @param session
     * @return
     */
    public static boolean checkLogin(HttpSession session) {
        Object[] params = new Object[]{"_json_att"};
        Object[] values = new Object[]{""};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(CHECK_LOGIN_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("checkLogin出错:" + o);
            return false;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getJSONObject("data").getBoolean("flag")) {
            LOGGER.info("用户已退出登陆状态，需要重新登陆");
            return false;
        } else {
            LOGGER.info("用户处于登陆状态");
            return true;
        }
    }

    /**
     * 查询某一天从A到B的列车及其余票信息
     *
     * @param session
     * @param date      日期 2020-01-24
     * @param shortFrom BJD
     * @param shortTo   SHH
     * @param useProxy  是否使用代理
     */
    public static JSONArray queryTickets(HttpSession session, String date, String shortFrom, String shortTo, boolean useProxy) {

        JSONArray jsonArray = null;
        //查询失败时重试一下
        for (int j = 0; j < 3; ++j) {
            String url = "leftTicket/query";
            Object get;
            if (useProxy) {
                get = session.httpsGet(LEFT_TICKET_INIT_URL, null);
            } else {
                get = session.httpsGetWithoutProxy(LEFT_TICKET_INIT_URL, null);
            }
            if (get instanceof String) {
                String content = (String) get;
                Pattern pattern = Pattern.compile("var CLeftTicketUrl = '(.*)'");
                Matcher m = pattern.matcher(content);
                if (m.find()) {
                    url = m.group(1);
                }
            }
            Object o;
            if (useProxy) {
                o = session.httpsGet(String.format(TICKET_URL, url, date, shortFrom, shortTo), null);
            } else {
                o = session.httpsGetWithoutProxy(String.format(TICKET_URL, url, date, shortFrom, shortTo), null);
            }
            if (!(o instanceof JSONObject)) {
                LOGGER.warn(String.format("查询失败，重新查询，当前重试次数%d", j + 1));
                continue;
            }
            JSONObject jsonObject = (JSONObject) o;
            jsonArray = jsonObject.getJSONObject("data").getJSONArray("result");
            break;
        }
        return jsonArray;
    }

    /**
     * 该接口还要调
     * TODO: 2020/1/20
     *
     * @param session
     * @param train_no
     * @param from_station_no
     * @param to_station_no
     * @param seat_types
     * @param date
     * @return
     */
    public static JSONObject queryTicketPrices(HttpSession session, String train_no, String from_station_no, String to_station_no, String seat_types, String date) {
        Map<String, String> head = new HashMap<>();
        head.put("Host", "kyfw.12306.cn");
        head.put("Accept", "*/*");
        session.httpsGet(String.format(TICKET_PRICES_INIT_URL, train_no,
                from_station_no, to_station_no, seat_types, date),
                head);
        Object object = session.httpsGet(String.format(TICKET_PRICES_URL, train_no,
                from_station_no, to_station_no, seat_types, date),
                head);
        if (object instanceof com.alibaba.fastjson.JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            if (jsonObject.getBoolean("status")) {
                return jsonObject.getJSONObject("data");
            }
        }
        return null;
    }

    /**
     * 点击预定时的下单请求
     *
     * @param session
     * @param secretStr     queryTickets查询结果中获取
     * @param trainDate     2020-02-01
     * @param backTrainDate 2020-02-03
     * @param fromStation   北京东
     * @param toStation     上海南
     * @return
     * @throws Exception
     */
    public static boolean submitOrderRequest(HttpSession session,
                                             String secretStr,
                                             String trainDate,
                                             String backTrainDate,
                                             String fromStation,
                                             String toStation) throws UnfinishedOrderException {
        try {
            secretStr = URLDecoder.decode(secretStr, "GBK");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        Object[] params = new Object[]{
                "secretStr",
                "train_date",
                "back_train_date",
                "tour_flag",
                "purpose_codes",
                "query_from_station_name",
                "query_to_station_name",
                "undefined"
        };
        Object[] values = new Object[]{
                secretStr,
                trainDate,
                backTrainDate,
                "dc",
                "ADULT",
                fromStation,
                toStation,
                ""
        };
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(SUBMIT_ORDER_REQUEST_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error(o);
            return false;
        }
        JSONObject res = (JSONObject) o;
        LOGGER.info(res);
        JSONArray arr = res.getJSONArray("messages");
        if (arr == null) {
            LOGGER.error("submitOrderRequest: res.getJSONArray(\"messages\") == null");
            return false;
        }
        String message = arr.toJSONString();
        if (message.contains("您还有未处理的订单，请您到")) {
            throw new UnfinishedOrderException();
        }
        Boolean status = res.getBoolean("status");
        if (status == null) {
            LOGGER.error("submitOrderRequest: res.getBoolean(\"status\") == null");
            return false;
        }
        return status;
    }

    /**
     * 获取token，用于获取乘客信息以及下单
     */
    public static JSONObject getTokenAndTicket(HttpSession session) {
        JSONObject res = new JSONObject();
        Object[] params = new Object[]{"_json_att"};
        Object[] values = new Object[]{""};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        String text = (String) session.httpsPost(INIT_DC_URL, paramsList, null);
        //token
        Pattern pattern = Pattern.compile("var globalRepeatSubmitToken = '(\\S+)'");
        Matcher m = pattern.matcher(text);
        if (m.find()) {
            res.put("token", m.group(1));
        }
        //ticketInfoForPassengerForm
        pattern = Pattern.compile("var ticketInfoForPassengerForm=(\\{.+\\})?");
        m = pattern.matcher(text);
        if (m.find()) {
            String str = m.group(1);
            JSONObject ticketInfoForPassengerForm = JSON.parseObject(str);
            res.put("ticketInfoForPassengerForm", ticketInfoForPassengerForm);
        }
        //order_request_params
        pattern = Pattern.compile("var orderRequestDTO=(\\{.+\\})?");
        m = pattern.matcher(text);
        if (m.find()) {
            String str = m.group(1);
            JSONObject order_request_params = JSON.parseObject(str);
            res.put("order_request_params", order_request_params);
        }
        LOGGER.info(res);
        return res;
    }

    /**
     * 获取乘客人，不下单时用
     */
    public static JSONArray getPassengers(HttpSession session) {
        Object[] params = new Object[]{"pageIndex", "pageSize"};
        Object[] values = new Object[]{"1", "100"};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(PASSENGERS_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error(o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        res = res.getJSONObject("data");
        if (res == null) {
            LOGGER.error("getPassengers: res.getJSONObject(\"data\") == null");
            return null;
        }
        JSONArray arr = res.getJSONArray("datas");
        if (arr == null) {
            LOGGER.error("getPassengers: res.getJSONArray(\"datas\") == null");
            return null;
        }
        return arr;
    }

    /**
     * 获取乘客人，在下单时要用
     */
    public static List<Passenger> getPassengersWithToken(HttpSession session, String token) {

        Object[] params = new Object[]{"_json_att", "REPEAT_SUBMIT_TOKEN"};
        Object[] values = new Object[]{"", token};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(PASSENGERS_WITH_TOKEN_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error(o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        Boolean status = res.getBoolean("status");
        if (status == null || !status) {
            return null;
        }
        res = res.getJSONObject("data");
        if (res == null) {
            LOGGER.error("getPassengersWithToken: res.getJSONObject(\"data\") == null");
            return null;
        }
        JSONArray arr = res.getJSONArray("normal_passengers");
        if (arr == null) {
            LOGGER.error("getPassengersWithToken: res.getJSONArray(\"normal_passengers\") == null");
            return null;
        }
        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < arr.size(); ++i) {
            passengers.add(JSON.parseObject(arr.getJSONObject(i).toJSONString(), Passenger.class));
        }
        return passengers;
    }

    /**
     * 检查选票人信息
     */
    public static boolean checkOrderInfo(HttpSession session, String token, String passengerTicketStr, String oldPassengerStr) {
        Object[] params = new Object[]{
                "cancel_flag",
                "bed_level_order_num",
                "passengerTicketStr",
                "oldPassengerStr",
                "tour_flag",
                "randCode",
                "whatsSelect",
                "_json_att",
                "REPEAT_SUBMIT_TOKEN"
        };
        Object[] values = new Object[]{
                "2",
                "000000000000000000000000000000",
                passengerTicketStr,
                oldPassengerStr,
                "dc",
                "",
                "1",
                "",
                token
        };
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(CHECK_ORDER_INFO_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error(o);
            return false;
        }
        JSONObject res = (JSONObject) o;
        LOGGER.info(res);
        Boolean status = res.getBoolean("status");
        if (status == null || !status) {
            LOGGER.error("checkOrderInfo: res.getBoolean(\"status\") == null");
            return false;
        }
        return true;
    }

    /**
     * 提交订单，进入排队队列
     *
     * @param session
     * @param data    getTokenAndTicket返回的对象
     * @param seat    座位类型：一等座
     * @param date    日期：2020-12-23
     * @return 队列中有多少票
     */
    public static JSONObject getQueueCount(HttpSession session, JSONObject data, String seat, String date) {
        JSONObject form = data.getJSONObject("ticketInfoForPassengerForm");
        JSONObject requestDTO = form.getJSONObject("queryLeftTicketRequestDTO");
        String train_no = requestDTO.getString("train_no");
        String stationTrainCode = requestDTO.getString("station_train_code");
        String seatType = SeatType.findNo(seat);
        String fromStationTelecode = requestDTO.getString("from_station");
        String toStationTelecode = requestDTO.getString("to_station");
        String leftTicket = form.getString("leftTicketStr");
        String purpose_codes = form.getString("purpose_codes");
        String train_location = form.getString("train_location");
        String REPEAT_SUBMIT_TOKEN = data.getString("token");
        String gmtDate = TimeFormatUtil.toBeijingGMTTime(date);
        Object[] params = new Object[]{
                "train_date",
                "train_no",
                "stationTrainCode",
                "seatType",
                "fromStationTelecode",
                "toStationTelecode",
                "leftTicket",
                "purpose_codes",
                "train_location",
                "_json_att",
                "REPEAT_SUBMIT_TOKEN"
        };
        Object[] values = new Object[]{
                gmtDate,
                train_no,
                stationTrainCode,
                seatType,
                fromStationTelecode,
                toStationTelecode,
                leftTicket,
                purpose_codes,
                train_location,
                "",
                REPEAT_SUBMIT_TOKEN
        };
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(GET_QUEUE_COUNT_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error(o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        res = res.getJSONObject("data");
        if (res == null) {
            LOGGER.error("getQueueCount: res.getJSONObject(\"data\") == null");
            return null;
        }
        LOGGER.info("getQueueCount: " + res);
        return res;
    }

    public static boolean confirmSingleForQueue(HttpSession session, JSONObject data, String passengerTicketStr, String oldPassengerStr) {
        JSONObject form = data.getJSONObject("ticketInfoForPassengerForm");
        JSONObject requestDTO = form.getJSONObject("queryLeftTicketRequestDTO");
        String purpose_codes = form.getString("purpose_codes");
        String key_check_isChange = form.getString("key_check_isChange");
        String leftTicketStr = form.getString("leftTicketStr");
        String train_location = form.getString("train_location");
        String seatDetailType = "000";
        String roomType = "00";
        String dwAll = "N";
        String whatsSelect = "1";
        String _json_at = "";
        String randCode = "";
        String choose_seats = "";
        String REPEAT_SUBMIT_TOKEN = data.getString("token");
        Object[] params = new Object[]{
                "passengerTicketStr",
                "oldPassengerStr",
                "purpose_codes",
                "key_check_isChange",
                "leftTicketStr",
                "train_location",
                "seatDetailType",
                "roomType",
                "dwAll",
                "whatsSelect",
                "_json_at",
                "randCode",
                "choose_seats",
                "REPEAT_SUBMIT_TOKEN"
        };
        Object[] values = new Object[]{
                passengerTicketStr,
                oldPassengerStr,
                purpose_codes,
                key_check_isChange,
                leftTicketStr,
                train_location,
                seatDetailType,
                roomType,
                dwAll,
                whatsSelect,
                _json_at,
                randCode,
                choose_seats,
                REPEAT_SUBMIT_TOKEN
        };
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(CONFIRM_SINGLE_FOR_QUEUE_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error(o);
            return false;
        }
        JSONObject res = (JSONObject) o;
        res = res.getJSONObject("data");
        if (res == null) {
            LOGGER.error("confirmSingleForQueue: res.getJSONObject(\"data\") == null");
            return false;
        }
        Boolean submitStatus = res.getBoolean("submitStatus");
        if (submitStatus == null) {
            LOGGER.info("confirmSingleForQueue: res.getBoolean(\"submitStatus\") == null");
            return false;
        }
        LOGGER.info("confirmSingleForQueue:" + res);
        return submitStatus;
    }

    public static String queryOrderWaitTime(HttpSession session, String token) throws UnfinishedOrderException {
        Object o = session.httpsGet(String.format(QUERY_ORDER_WAIT_TIME_URL, System.currentTimeMillis(), token), null);
        LOGGER.info("queryOrderWaitTime：" + o);
        if (!(o instanceof JSONObject)) {
            LOGGER.error(o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        res = res.getJSONObject("data");
        if (res == null) {
            LOGGER.error("queryOrderWaitTime: res.getJSONObject(\"data\") == null");
            return null;
        }
        String msg = res.getString("msg");
        if (msg != null && msg.contains("已订车票与本次购票行程冲突")) {
            LOGGER.error(msg);
            throw new UnfinishedOrderException();
        }
        String orderId = res.getString("orderId");
        if (orderId == null) {
            LOGGER.error("queryOrderWaitTime: res.getString(\"orderId\") == null");
            return null;
        }
        LOGGER.info("queryOrderWaitTime:" + res);
        return orderId;
    }

    public static boolean resultOrderForDcQueue(HttpSession session, String orderId, String token) {
        Object[] params = new Object[]{"orderSequence_no", "_json_att", "REPEAT_SUBMIT_TOKEN"};
        Object[] values = new Object[]{orderId, "", token};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(RESULT_ORDER_FOR_QUEUE_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error(o);
            return false;
        }
        JSONObject res = (JSONObject) o;
        res = res.getJSONObject("data");
        if (res == null) {
            LOGGER.error("resultOrderForDcQueue: res.getJSONObject(\"data\") == null");
            return false;
        }
        Boolean submitStatus = res.getBoolean("submitStatus");
        if (submitStatus == null) {
            LOGGER.info("resultOrderForDcQueue: res.getBoolean(\"submitStatus\") == null");
            return false;
        }
        LOGGER.info("resultOrderForDcQueue:" + res);
        return submitStatus;
    }

    public static boolean checkFace(HttpSession session, String secretStr, String seatType) {
        String ticker = SeatType.findNo(seatType);
        Object[] params = new Object[]{"secretList", "_json_att"};
        Object[] values = new Object[]{secretStr + "#" + ticker + "|", ""};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(AN_CHECK_FACE_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("checkFace: " + o);
            return false;
        }
        JSONObject res = (JSONObject) o;
        JSONObject data = res.getJSONObject("data");
        JSONArray messages = res.getJSONArray("messages");
        if (data == null) {
            LOGGER.error("checkFace: res.getJSONObject(\"data\") == null");
            return false;
        }
        Boolean flag = data.getBoolean("face_flag");
        if (flag == null) {
            LOGGER.error("checkFace: data.getBoolean(\"face_flag\") == null");
            return false;
        }
        if (!flag && messages != null) {
            LOGGER.warn(String.format("人脸验证不通过:【%s】", messages.toJSONString()));
            return false;
        }
        return true;
    }

    public static JSONObject getSuccessRate(HttpSession session, String secretStr, String seatType) {
        String ticker = SeatType.findNo(seatType);
        Object[] params = new Object[]{"successSecret", "_json_att"};
        Object[] values = new Object[]{secretStr + "#" + ticker, ""};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(AN_SUCCESS_RATE_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("getSuccessRate: " + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        LOGGER.info("getSuccessRate: " + res);
        return res;
    }

    public static boolean submitANOrderRequest(HttpSession session, String secretStr, String seatType) {
        String ticker = SeatType.findNo(seatType);
        Object[] params = new Object[]{"secretList", "_json_att"};
        Object[] values = new Object[]{secretStr + "#" + ticker + "|", ""};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(AN_SUBMIT_ORDER_REQUEST_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("submitANOrderRequest: " + o);
            return false;
        }
        JSONObject res = (JSONObject) o;
        JSONObject data = res.getJSONObject("data");
        if (data == null) {
            LOGGER.error("submitANOrderRequest: res.getJSONObject(\"data\") == null");
            return false;
        }
        Boolean flag = data.getBoolean("flag");
        if (flag == null) {
            LOGGER.error("submitANOrderRequest: data.getJSONObject(\"face_flag\") == null");
            return false;
        }
        if (!flag) {
            LOGGER.warn("submitANOrderRequest: face_flag=" + false);
            return false;
        }
        return true;
    }

    public static JSONObject initPassengerApi(HttpSession session) {
        Object o = session.httpsPost(AN_PASSENGER_INIT_API_URL, null, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("initPassengerApi: " + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        LOGGER.info("initPassengerApi: " + res);
        return res;
    }

    public static boolean confirmHB(HttpSession session, String passengerInfo, String jzParam, String trainNo, String seatType) {
        session.printCookie();
        String hbBrain = trainNo + "," + SeatType.findNo(seatType) + "#";
        Object[] params = new Object[]{"passengerInfo", "jzParam", "hbTrain", "lkParam"};
        Object[] values = new Object[]{passengerInfo, jzParam, hbBrain, ""};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(AN_CONFIRM_HB_URL, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("confirmHB: " + o);
            return false;
        }
        JSONObject res = (JSONObject) o;
        JSONObject data = res.getJSONObject("data");
        if (data == null) {
            LOGGER.error("confirmHB: res.getJSONObject(\"data\") == null");
            return false;
        }
        Boolean flag = data.getBoolean("flag");
        if (flag == null) {
            LOGGER.error("confirmHB: data.getBoolean(\"flag\") == null");
            return false;
        }
        if (!flag) {
            LOGGER.warn("confirmHB: flag=" + false);
            return false;
        }
        return true;
    }

    public static boolean queryANQueue(HttpSession session) throws UnfinishedOrderException {
        JSONObject res;
        Map<String, String> head = new HashMap<>();
        head.put("Referer", "https://kyfw.12306.cn/otn/leftTicket/init");
        head.put("Host", "kyfw.12306.cn");
        for (int i = 0; i < 5; ++i) {
            Object o = session.httpsPost(AN_QUERY_QUEUE, null, head);
            if (!(o instanceof JSONObject)) {
                SleepUtil.sleepRandomTime(500, 1000);
                continue;
            }
            res = (JSONObject) o;
            JSONObject data = res.getJSONObject("data");
            if (data == null) {
                LOGGER.error("queryANQueue: res.getJSONObject(\"data\") == null");
                SleepUtil.sleepRandomTime(500, 1000);
                continue;
            }
            Integer status = data.getInteger("status");
            if (status == null) {
                LOGGER.error("queryANQueue: data.getInteger(\"status\") == null");
                SleepUtil.sleepRandomTime(500, 1000);
                continue;
            }
            if (status != 1) {
                LOGGER.warn(String.format("queryANQueue失败：【%s】", data.getString("msg")));
                if (data.getString("msg").contains("已订车票与本次购票行程冲突")) {
                    throw new UnfinishedOrderException();
                }
                SleepUtil.sleepRandomTime(500, 1000);
                continue;
            }
            LOGGER.info("queryANQueue: " + res);
            return true;
        }
        return false;
    }

    public static JSONObject queryNoCompleteOrder(HttpSession session) {
        Object[] params = new Object[]{"_json_att"};
        Object[] values = new Object[]{""};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(QUERY_NO_COMPLETE_ORDER, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("queryNoCompleteOrder:" + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("queryNoCompleteOrder:" + res);
            return null;
        } else {
            LOGGER.info("请求成功");
            return res;
        }
    }

    public static boolean cancelNoCompleteOrder(HttpSession session, String sequenceNo) {
        Object[] params = new Object[]{"sequence_no", "cancel_flag"};
        Object[] values = new Object[]{sequenceNo, "cancel_order"};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(CANCEL_NO_COMPLETE_ORDER, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("cancelNoCompleteOrder:" + o);
            return false;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("cancelNoCompleteOrder:" + res);
            return false;
        } else {
            LOGGER.info("请求成功");
            return true;
        }
    }

    public static JSONObject queryNoCompleteAnOrder(HttpSession session) {
        Object o = session.httpsPost(QUERY_NO_COMPLETE_AN_ORDER, null, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("queryNoCompleteAnOrder:" + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("queryNoCompleteAnOrder:" + res);
            return null;
        } else {
            LOGGER.info("请求成功");
            return res;
        }
    }


    public static JSONObject queryMyUntraveledOrder(HttpSession session) {
        Object[] params = new Object[]{"come_from_flag", "pageIndex", "pageSize", "query_where", "queryStartDate", "queryEndDate", "queryType", "sequeue_train_name"};
        Object[] values = new Object[]{"my_order", 0, 8, "G", TimeFormatUtil.getDateStr(-120), TimeFormatUtil.getDateStr(0), 1, ""};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(QUERY_MY_ORDER, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("queryMyUntraveledOrder:" + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("queryMyUntraveledOrder:" + res);
            return null;
        } else {
            LOGGER.info("请求成功");
            return res;
        }
    }

    public static JSONObject queryMyUnChashAnOrder(HttpSession session) {
        Object[] params = new Object[]{"page_no", "query_start_date", "query_end_date"};
        Object[] values = new Object[]{0, TimeFormatUtil.getDateStr(-120), TimeFormatUtil.getDateStr(120)};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(QUERY_NO_CASH_AN_ORDER, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("queryMyUntraveledOrder:" + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("queryMyUntraveledOrder:" + res);
            return null;
        } else {
            LOGGER.info("请求成功");
            return res;
        }
    }

    //https://kyfw.12306.cn/otn/afterNateOrder/queryProcessedHOrder

    /**
     * 查询已处理的候补订单
     * @param session
     * @return
     */
    public static JSONObject queryProcessedHOrder(HttpSession session) {
        Object[] params = new Object[]{"page_no", "query_start_date", "query_end_date"};
        Object[] values = new Object[]{0, TimeFormatUtil.getDateStr(-120), TimeFormatUtil.getDateStr(120)};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(QUERY_PROCESSED_AN_ORDER, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("queryProcessedHOrder:" + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("queryProcessedHOrder:" + res);
            return null;
        } else {
            LOGGER.info("请求成功");
            return res;
        }
    }

    public static JSONObject reserveReturnCheck(HttpSession session, String reserveNo) {
        Object[] params = new Object[]{"sequence_no"};
        Object[] values = new Object[]{reserveNo};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(CANCEL_NO_CASH_AN_ORDER1, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("reserveReturnCheck:" + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("reserveReturnCheck:" + res);
            return null;
        } else {
            return res;
        }
    }

    public static JSONObject reserveReturn(HttpSession session, String reserveNo) {
        Object[] params = new Object[]{"sequence_no"};
        Object[] values = new Object[]{reserveNo};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(CANCEL_NO_CASH_AN_ORDER2, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("reserveReturn:" + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("reserveReturn:" + res);
            return null;
        } else {
            return res;
        }
    }


    public static JSONObject reserveReturnSuccessApi(HttpSession session) {
        Object o = session.httpsPost(CANCEL_NO_CASH_AN_ORDER3, null, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("reserveReturnSuccessApi:" + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("reserveReturnSuccessApi:" + res);
            return null;
        } else {
            return res;
        }
    }

    public static JSONObject cancelNoPaidAnOrder(HttpSession session, String reserveNo) {
        Object[] params = new Object[]{"sequence_no"};
        Object[] values = new Object[]{reserveNo};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        Object o = session.httpsPost(CANCEL_NO_PAID_AN_ORDER, paramsList, null);
        if (!(o instanceof JSONObject)) {
            LOGGER.error("cancelNoPaidAnOrder:" + o);
            return null;
        }
        JSONObject res = (JSONObject) o;
        if (!res.getBoolean("status")) {
            LOGGER.info("cancelNoPaidAnOrder:" + res);
            return null;
        } else {
            return res;
        }
    }


    private static class AlgHelper {
        private final static Map<String, String> data = new TreeMap<>();
        private final static Map<String, String> table = new HashMap<>();
        private final static Random random = new Random();
        private final static AtomicInteger index = new AtomicInteger(0);
        private final static String[] cookieCodes = new String[]{
                "FGEk4w5eRZl9IzvDIxO9KKPTJP59E9it",
                "FGE_l0XpR4Kp5DAUduAakGLLkP4Jwg-R",
                "FGHMOaIK-x1IV7nRvCP9KYiC_ft-LNAT",
                "FGGuwMQn9jAOSoEc6iOPEtK6dslrpCha",
                "FGHjNjFGHbjB0XksOK6JvkqcKwBxNGnt",
                "FGEJiTzNIFlFkErQmUYxfaLrciObGcAv"
        };

        static {
            table.put("localCode", "lEnu");
            table.put("hasLiedOs", "ci5c");
            table.put("scrAvailSize", "TeRS");
            table.put("sessionStorage", "HVia");
            table.put("indexedDb", "3sw-");
            table.put("scrHeight", "5Jwy");
            table.put("cookieEnabled", "VPIf");
            table.put("flashVersion", "dzuS");
            table.put("adblock", "FMQw");
            table.put("plugins", "ks0Q");
            table.put("scrAvailWidth", "E-lJ");
            table.put("touchSupport", "wNLf");
            table.put("scrAvailHeight", "88tV");
            table.put("hasLiedBrowser", "2xC5");
            table.put("mimeTypes", "jp76");
            table.put("cpuClass", "Md7A");
            table.put("browserLanguage", "q4f3");
            table.put("systemLanguage", "e6OK");
            table.put("scrColorDepth", "qmyu");
            table.put("appcodeName", "qT7b");
            table.put("javaEnabled", "yD16");
            table.put("doNotTrack", "VEek");
            table.put("userLanguage", "hLzX");
            table.put("userAgent", "0aew");
            table.put("localStorage", "XM7l");
            table.put("timeZone", "q5aJ");
            table.put("browserName", "-UVA");
            table.put("browserVersion", "d435");
            table.put("scrDeviceXDPI", "3jCe");
            table.put("hasLiedResolution", "3neK");
            table.put("online", "9vyE");
            table.put("historyList", "kU5z");
            table.put("cookieCode", "VySQ");
            table.put("scrWidth", "ssI5");
            table.put("webSmartID", "E3gR");
            table.put("srcScreenSize", "tOHY");
            table.put("storeDb", "Fvje");
            table.put("jsFonts", "EOQP");
            table.put("openDatabase", "V8vl");
            table.put("appMinorVersion", "qBVW");
            table.put("os", "hAqN");
            table.put("hasLiedLanguages", "j5po");
        }

        static {
            data.put("adblock", "0");
            data.put("browserLanguage", "zh-CN");
            data.put("cookieCode", "");
            data.put("cookieEnabled", "1");
            data.put("custID", "133");
            data.put("doNotTrack", "unknown");
            data.put("flashVersion", "0");
            data.put("javaEnabled", "0");
            data.put("jsFonts", "c227b88b01f5c513710d4b9f16a5ce52");
            data.put("mimeTypes", "52d67b2a5aa5e031084733d5006cc664");
            data.put("os", "MacIntel");
            data.put("platform", "WEB");
            data.put("plugins", "d22ca0b81584fbea62237b14bd04c866");
            data.put("scrAvailSize", (random.nextInt(500) + 500) + "x1680");
            data.put("srcScreenSize", "24xx1050x1680");
            data.put("storeDb", "i1l1o1s1");
            data.put("timeZone", "-8");
            data.put("touchSupport", "99115dfb07133750ba677d055874de87");
            data.put("userAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0." +
                    (random.nextInt(2000) + 5000) + ".0 Safari/537.36");
            data.put("webSmartID", "24a381e8423bbbfee90f76c76d10c077");
        }

        private static String getCookieCode() {
            if (index.get() >= cookieCodes.length) {
                index.set(0);
            }
            String coo = cookieCodes[index.getAndAdd(1)];
            LOGGER.info(String.format("使用的cookieCode为【%s】", coo));
            return coo;
        }

        public static Map<String, String> getData() {
            //以下值是需要变化的
            data.put("cookieCode", getCookieCode());
            data.put("scrAvailSize", (random.nextInt(500) + 500) + "x1680");
            data.put("userAgent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0." +
                    (random.nextInt(2000) + 5000) + ".0 Safari/537.36");
//            f75bd56aa7ab9c355a2ed6ff4573916d
            data.put("webSmartID", "24a381e8423bbbfee90f76c76d10c077");
            return data;
        }

        static String getHashCode(String script, String dataStr) {
            JSFunction hashAlg = JSParser.getFunc(script, "hashAlg");
            if (hashAlg == null) {
                return null;
            }
            String shaFuncName = null;
            List<JSFunction> funcs = JSParser.getFuncs(script);
            StringBuilder funcsStr = new StringBuilder();
            for (JSFunction jsFunc : funcs) {
                if (jsFunc.getTotal().contains("SHA256")) {
                    shaFuncName = jsFunc.getName();
                }
                funcsStr.append(jsFunc.getTotal()).append("\n");
            }
            //函数的语句
            List<String> statements = getStatements(hashAlg.getTotal());
            //所有变量
            Map<String, Object> map = new TreeMap<>();
            for (String s : statements) {
                String[] arr = s.split("=");
                if (arr.length == 2 && arr[0].length() == 1) {
                    map.put(arr[0], dataStr);
                }
            }
            //参数列表
            StringBuilder builder = new StringBuilder();
            for (String input : map.keySet()) {
                builder.append(input).append(",");
            }
            //按照SHA256函数进行分割
            List<String> functions = new ArrayList<>();
            List<String> shaIn = new ArrayList<>();
            List<String> shaOut = new ArrayList<>();
            builder = new StringBuilder();
            for (String s : statements) {
                if (s.contains("SHA256") || (shaFuncName != null && s.contains(shaFuncName + "("))) {
                    //a=R.SHA256(c).toString(R.enc.Base64)
                    //a=za(c)
                    shaIn.add(s.substring(s.indexOf("(") + 1, s.indexOf(")")));
                    shaOut.add(s.substring(0, s.indexOf("=")));
                    functions.add(String.format(
                            "function func%d(%s){\n%sreturn [%s];\n}",
                            functions.size(),
                            String.join(",", getKeys(map)),
                            builder.toString(),
                            String.join(",", getKeys(map)))
                    );
                    builder = new StringBuilder();
                } else {
                    builder.append(s).append("\n");
                }
            }
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine js = manager.getEngineByName("javascript");
            try {
                for (int i = 0; i < functions.size(); ++i) {
                    js.eval(funcsStr.toString() + String.join("\n", functions));
                    Invocable invocable = (Invocable) js;
                    ScriptObjectMirror mirror = (ScriptObjectMirror) invocable.invokeFunction("func" + i, getValues(map));
                    update(map, mirror.values());
                    map.put(shaOut.get(i), sha256((String) map.get(shaIn.get(i))));
                }
            } catch (NoSuchMethodException | ScriptException e) {
                LOGGER.error(funcsStr.toString());
                LOGGER.error(String.join("\n", functions));
                LOGGER.error(e.getMessage());
            }
            return (String) map.get(shaOut.get(shaOut.size() - 1));
        }

        private static Object[] getValues(Map<String, Object> map) {
            Object[] values = new Object[map.size()];
            int i = 0;
            for (String key : map.keySet()) {
                values[i++] = map.get(key);
            }
            return values;
        }

        private static String[] getKeys(Map<String, Object> map) {
            String[] keys = new String[map.size()];
            int i = 0;
            for (String key : map.keySet()) {
                keys[i++] = key;
            }
            return keys;
        }

        private static void update(Map<String, Object> map, Collection<Object> values) {
            Object[] objects = new Object[values.size()];
            objects = values.toArray(objects);
            int i = 0;
            for (String key : map.keySet()) {
                map.put(key, objects[i++]);
            }
        }

        private static List<String> getStatements(String hashAlgFunc) {
            int a = hashAlgFunc.lastIndexOf("return new");
            hashAlgFunc = hashAlgFunc.substring(0, a);
            a = hashAlgFunc.indexOf("}");
            hashAlgFunc = hashAlgFunc.substring(a + 1);
            a = hashAlgFunc.indexOf("}");
            hashAlgFunc = hashAlgFunc.substring(a + 1);
            List<String> statements = new ArrayList<>();
            String[] split = hashAlgFunc.split(";");
            for (int i = 0; i < split.length; ) {
                String statement;
                if (split[i].startsWith("for")) {
                    statement = split[i] + ";" + split[i + 1] + ";" + split[i + 2] + ";";
                    i += 3;
                } else {
                    statement = split[i] + ";";
                    i += 1;
                }
                statements.add(statement.replaceAll("\n", ""));
            }
            return statements;
        }

        private static String sha256(String c) {
            String result = null;
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(c.getBytes(StandardCharsets.UTF_8));
                result = Base64.getEncoder().encodeToString(messageDigest.digest());
            } catch (NoSuchAlgorithmException ex) {
                LOGGER.error(ex.getMessage());
            }
            if (result != null) {
                result = result.replaceAll("\\+", "-").replaceAll("/", "_").replaceAll("=", "");
            }
            return result;
        }
    }

    public static class Helper {

        //如果有多个乘客，则将字符串按_拼接
        static String getOnePassengerTicketStr(Passenger passenger, String seat) {
            //座位类型
            String builder = SeatType.findNo(seat) + "," +
                    //固定值
                    "0" + "," +
                    //票类型(成人/儿童)
                    passenger.getPassenger_type() + "," +
                    //乘客名字
                    passenger.getPassenger_name() + "," +
                    //身份类型(身份证/军官证….)
                    passenger.getPassenger_id_type_code() + "," +
                    //证件号
                    passenger.getPassenger_id_no() + "," +
                    //电话号码
                    (passenger.getMobile_no().isEmpty() ? passenger.getPhone_no() : passenger.getMobile_no()) + "," +
                    //固定值
                    "N" + "," +
                    //allEncStr
                    passenger.getAllEncStr();
            return builder;
        }

        public static String getMultiPassengerTicketStr(List<Passenger> passengers, List<String> seats) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < passengers.size() && i < seats.size(); ++i) {
                String one = getOnePassengerTicketStr(passengers.get(i), seats.get(i));
                builder.append(one).append("_");
            }
            return builder.substring(0, builder.length() - 1);
        }

        static String getOneOldPassengerStr(Passenger passenger) {
            StringBuilder builder = new StringBuilder();
            //姓名
            builder.append(passenger.getPassenger_name()).append(",");
            //证件类型
            builder.append(passenger.getPassenger_id_type_code()).append(",");
            //证件号
            builder.append(passenger.getPassenger_id_no()).append(",");
            //乘客类型
            builder.append(passenger.getPassenger_type()).append("_");
            return builder.toString();
        }

        public static String getMultiOldPassengerStr(List<Passenger> passengers) {
            StringBuilder builder = new StringBuilder();
            for (Passenger passenger : passengers) {
                String one = getOneOldPassengerStr(passenger);
                builder.append(one);
            }
            return builder.toString();
        }

    }

}
