package com.wshy.billcheckdbserver.serviceImpl;

import com.wshy.billcheckdbserver.HttpRequest.DBServerRequest;
import com.wshy.billcheckdbserver.Utils.JsonUtils;
import com.wshy.billcheckdbserver.Utils.PayUtils;
import com.wshy.billcheckdbserver.config.BillCheckConstants;
import com.wshy.billcheckdbserver.serviceInf.GetOutBillAccServiceInf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/17
 **/
@Service
public class GetOutBillAccServiceImpl implements GetOutBillAccServiceInf {
    private static final Logger logger = LoggerFactory.getLogger (GetOutBillAccServiceImpl.class);

    @Autowired
    private DBServerRequest dbServerRequest;

    /**
     * 获取外部第三方对账数据
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> getOutThirdBill (Map <String, Object> map) throws Exception {
        Map<String,Object> req = new HashMap <> ();     //请求数据库服务的map
        Map<String,Object> resp = new HashMap <> ();    //数据库服务返回map
        //Map<String, Object> tradeparammap = new HashMap<String, Object>();

        req.put ("tradeType","Out_ThirdData");
        req.put ("tradeParam",map);
        req.put ("sign", PayUtils.builderSignStr (map, (String) BillCheckConstants.billcheckConstantsmap.get ("md5Key")));  //teadeparam部分签名

        String data = JsonUtils.MapToJson (req);
        resp = dbServerRequest.dopost ((String) BillCheckConstants.billcheckConstantsmap.get ("dbURL1"),data);

        return resp;
    }

    /**
     * 获取外部his对账数据
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> getOutHisBill (Map <String, Object> map) throws Exception {
        Map<String,Object> req = new HashMap <String, Object> ();     //请求数据库服务的map
        Map<String,Object> resp = new HashMap <String, Object> ();    //数据库服务返回map
        //Map<String, Object> tradeparammap = new HashMap<String, Object>();

        req.put ("tradeType","Out_HisData");
        req.put ("tradeParam",map);
        req.put ("sign", PayUtils.builderSignStr (map, (String) BillCheckConstants.billcheckConstantsmap.get ("md5Key")));  //teadeparam部分签名

        String data = JsonUtils.MapToJson (req);
        resp = dbServerRequest.dopost ((String) BillCheckConstants.billcheckConstantsmap.get ("dbURL2"),data);

        return resp;
    }
}
