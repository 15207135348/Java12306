package com.yy.other.config;


public interface RailWayConfig {

    String GET_STATIONS_URL = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js";
    //与登陆相关
    String GET_JS_URL = "https://kyfw.12306.cn/otn/HttpZF/GetJS";
    String LOG_DEVICE_URL = "https://kyfw.12306.cn/otn/HttpZF/logdevice";
    String IMAGE_URL = "https://kyfw.12306.cn/passport/captcha/captcha-image64?login_site=E&module=login&rand=sjrand&%d&callback=jQuery19108016482864806321_1554298927290&_=1554298927293";
    String CHECK_ANSWER_URL = "https://kyfw.12306.cn/passport/captcha/captcha-check?callback=jQuery19108016482864806321_1554298927290&answer=%s&rand=sjrand&login_site=E&_=%d";
    String LOGIN_URL = "https://kyfw.12306.cn/passport/web/login";
    String UAMTK_URL = "https://kyfw.12306.cn/passport/web/auth/uamtk";
    String UAMAUTHCLIENT_URL = "https://kyfw.12306.cn/otn/uamauthclient";
    String CHECK_LOGIN_URL = "https://kyfw.12306.cn/otn/login/checkUser";
    //与查票相关
    String TICKET_URL = "https://kyfw.12306.cn/otn/%s?leftTicketDTO.train_date=%s&leftTicketDTO.from_station=%s&leftTicketDTO.to_station=%s&purpose_codes=ADULT";
    String LEFT_TICKET_INIT_URL = "https://kyfw.12306.cn/otn/leftTicket/init";
    String TICKET_PRICES_URL = "https://kyfw.12306.cn/otn/leftTicket/queryTicketPriceFL?train_no=%s&from_station_no=%s&to_station_no=%s&seat_types=%s&train_date=%s";
    String TICKET_PRICES_INIT_URL = "https://kyfw.12306.cn/otn/leftTicket/queryTicketPrice?train_no=%s&from_station_no=%s&to_station_no=%s&seat_types=%s&train_date=%s";
    //与获取乘客信息相关
    String PASSENGERS_URL = "https://kyfw.12306.cn/otn/passengers/query";
    //与下单相关
    String SUBMIT_ORDER_REQUEST_URL = "https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest";
    String INIT_DC_URL = "https://kyfw.12306.cn/otn/confirmPassenger/initDc";
    String PASSENGERS_WITH_TOKEN_URL = "https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs";
    String CHECK_ORDER_INFO_URL = "https://kyfw.12306.cn/otn/confirmPassenger/checkOrderInfo";
    String GET_QUEUE_COUNT_URL = "https://kyfw.12306.cn/otn/confirmPassenger/getQueueCount";
    String CONFIRM_SINGLE_FOR_QUEUE_URL = "https://kyfw.12306.cn/otn/confirmPassenger/confirmSingleForQueue";
    String QUERY_ORDER_WAIT_TIME_URL = "https://kyfw.12306.cn/otn/confirmPassenger/queryOrderWaitTime?random=%d&tourFlag=dc&_json_att=&REPEAT_SUBMIT_TOKEN=%s";
    String RESULT_ORDER_FOR_QUEUE_URL = "https://kyfw.12306.cn/otn/confirmPassenger/resultOrderForDcQueue";
    //候补订单有关
    String AN_CHECK_FACE_URL = "https://kyfw.12306.cn/otn/afterNate/chechFace";
    String AN_SUCCESS_RATE_URL = "https://kyfw.12306.cn/otn/afterNate/getSuccessRate";
    String AN_SUBMIT_ORDER_REQUEST_URL = "https://kyfw.12306.cn/otn/afterNate/submitOrderRequest";
    String AN_PASSENGER_INIT_API_URL = "https://kyfw.12306.cn/otn/afterNate/passengerInitApi";
    String AN_CONFIRM_HB_URL = "https://kyfw.12306.cn/otn/afterNate/confirmHB";
    String AN_QUERY_QUEUE = "https://kyfw.12306.cn/otn/afterNate/queryQueue";
    //查询未完成订单
    String QUERY_NO_COMPLETE_ORDER = "https://kyfw.12306.cn/otn/queryOrder/queryMyOrderNoComplete";
    //取消未完成订单
    String CANCEL_NO_COMPLETE_ORDER = "https://kyfw.12306.cn/otn/queryOrder/cancelNoCompleteMyOrder";
    //查询我的未出行订单或者历史订单
    String QUERY_MY_ORDER="https://kyfw.12306.cn/otn/queryOrder/queryMyOrder";
    //查询未完成的候补订单
    String QUERY_NO_COMPLETE_AN_ORDER = "https://kyfw.12306.cn/otn/afterNateOrder/queryQueue";
    //查询未兑现的候补订单
    String QUERY_NO_CASH_AN_ORDER = "https://kyfw.12306.cn/otn/afterNateOrder/queryUnHonourHOrder";
    //查询已处理的候补订单
    String QUERY_PROCESSED_AN_ORDER = "https://kyfw.12306.cn/otn/afterNateOrder/queryProcessedHOrder";
    //取消未支付的候补订单
    String CANCEL_NO_PAID_AN_ORDER = "https://kyfw.12306.cn/otn/afterNateOrder/cancelNotComplete";
    //取消未兑现的候补订单
    String CANCEL_NO_CASH_AN_ORDER1= "https://kyfw.12306.cn/otn/afterNateOrder/reserveReturnCheck";
    String CANCEL_NO_CASH_AN_ORDER2= "https://kyfw.12306.cn/otn/afterNateOrder/reserveReturn";
    String CANCEL_NO_CASH_AN_ORDER3= "https://kyfw.12306.cn/otn/afterNateOrder/reserveReturnSuccessApi";

}
