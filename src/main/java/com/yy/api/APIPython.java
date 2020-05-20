package com.yy.api;

import com.yy.util.HttpClient;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;

import java.util.List;

import static com.yy.config.PythonConfig.ANSWER_URL;

public class APIPython {

    private static final Logger LOGGER = Logger.getLogger(APIPython.class);



    public static String getAnswer(String base64Image) {
        Object [] params = new Object[]{"base64_image"};
        Object [] values = new Object[]{base64Image};
        List<NameValuePair> paramsList = HttpClient.getParams(params, values);
        String answer;
        try {
            answer = (String) HttpClient.sendPost(ANSWER_URL, paramsList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return null;
        }
        if (answer == null) {
            return null;
        }
        LOGGER.info("POST返回信息：" + answer);
        return answer;
    }
}
