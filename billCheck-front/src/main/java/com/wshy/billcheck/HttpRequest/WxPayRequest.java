package com.wshy.billcheck.HttpRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/17
 **/
@Service
public class WxPayRequest {
    private final static Logger logger = LoggerFactory.getLogger(WxPayRequest.class);

    public Map <String, Object> dopost(String url, String data) {
        HttpClient client;
        Map<String, Object> strResult = new HashMap <String, Object> ();
        PostMethod post = new PostMethod(url.toString());
        try {
            logger.info("WxPayRequest dopost data = " + data);

            //HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
            SimpleHttpConnectionManager httpConnectionManager = new SimpleHttpConnectionManager(true);
            HttpConnectionManagerParams params = httpConnectionManager.getParams();
            params.setConnectionTimeout(5000);
            params.setSoTimeout(20000);
            params.setDefaultMaxConnectionsPerHost(1000);
            params.setMaxTotalConnections(1000);

            client = new HttpClient(httpConnectionManager);
            client.getParams().setContentCharset("utf-8");
            client.getParams().setHttpElementCharset("utf-8");

            RequestEntity requestEntity = new StringRequestEntity (data, "application/json", "utf-8");
            post.setRequestEntity(requestEntity);
            post.getParams().setContentCharset("utf-8");

            client.executeMethod(post);
            if (post.getStatusCode() == 200) {
                strResult.put("statuscode", post.getStatusCode() + "");
                strResult.put("conResult", post.getResponseBodyAsString() + "");
            } else {
                strResult.put("statuscode", post.getStatusCode() + "");
            }
            logger.info("WxPayRequest strResult = " + strResult);
        } catch (Exception e) {
            strResult.put("statuscode", "999");
        } finally {
            post.releaseConnection();
        }
        return strResult;
    }
}
