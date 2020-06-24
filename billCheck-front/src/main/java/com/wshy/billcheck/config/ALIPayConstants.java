package com.wshy.billcheck.config;


import com.wshy.billcheck.Utils.JsonUtils;
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
public class ALIPayConstants {
    	/*
	public static String URL = "http://www.ggzzrj.xyz:8848/Alipay/ApiService";//支付宝网关

	public static String pId = "2088221973891545";//商户号

	public static String appId = "2016061301512892";//应用id

	public static String md5Key = "HtczW2pVi22aibK1HVfoTSz8KGEyCdiT";
	*/

    public static Map <String, Object> alipayConstantsmap = new HashMap <String, Object>();

    static {
        ClassPathResource classPathResource = new ClassPathResource("config/alipay.json");
        try {
            String alipayData =  IOUtils.toString(classPathResource.getInputStream(), Charset.forName("UTF-8"));
            alipayConstantsmap = JsonUtils.JsonToMapObj(alipayData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
