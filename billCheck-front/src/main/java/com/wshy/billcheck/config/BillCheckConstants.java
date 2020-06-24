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
 * @data 2020/6/19
 **/
public class BillCheckConstants {
    public static Map <String, Object> billcheckConstantsmap = new HashMap <String, Object> ();

    static {
        ClassPathResource classPathResource = new ClassPathResource("config/billcheck.json");
        try {
            String billcheckData =  IOUtils.toString(classPathResource.getInputStream(), Charset.forName("UTF-8"));
            billcheckConstantsmap = JsonUtils.JsonToMapObj(billcheckData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
