package com.wshy.billcheckdbserver.config;

import com.wshy.billcheckdbserver.Utils.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public class WXPayConstants {
    public static final String FIELD_SIGN = "sign";

    public static Map <String, Object> wxpayConstantsmap = new HashMap <String, Object> ();

    static {
        ClassPathResource classPathResource = new ClassPathResource("config/wxpay.json");
        try {
            String wxpayData =  IOUtils.toString(classPathResource.getInputStream(), Charset.forName("UTF-8"));
            wxpayConstantsmap = JsonUtils.JsonToMapObj(wxpayData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
