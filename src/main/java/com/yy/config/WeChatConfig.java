package com.yy.config;


public interface WeChatConfig {
    
    String appID = "wxb81ef370325715b1";
    String appSecret = "31bb52918104819c42b6e99b0c65e1c5";
    String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    String GER_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    String SEND_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=%s";
    String templateCommonTicket = "K_hAQJeBiVnBwblrF6lB0qZthvlaNFtC_20RgYM3HHc";
    String templateAlternateTicket = "K_hAQJeBiVnBwblrF6lB0pYtVX5ag5-D2sRikUtoouA";
}
