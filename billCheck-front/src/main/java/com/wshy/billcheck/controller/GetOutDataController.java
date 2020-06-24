package com.wshy.billcheck.controller;

import com.wshy.billcheck.Exception.CenterErrorException;
import com.wshy.billcheck.Exception.ResponseInfo;
import com.wshy.billcheck.Exception.ResponseMsgEnum;
import com.wshy.billcheck.Utils.JsonUtils;
import com.wshy.billcheck.Utils.PayUtils;
import com.wshy.billcheck.Utils.ResponseUtils;
import com.wshy.billcheck.serviceImpl.GGpayServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/19
 **/
@RestController
@RequestMapping(value = "/lay",method = RequestMethod.POST)
public class GetOutDataController {

    private static final Logger logger = LoggerFactory.getLogger (GetOutDataController.class);

    @Autowired
    private GGpayServiceImpl ggpayService;

    /**
     * 获取外部第三方账单数据
     * @param req
     * @param resp
     * @param jsonreq
     * @return
     * @throws Exception
     */
    @RequestMapping("/getOutThirdData")
    public ResponseInfo getOutThirdBillData(HttpServletRequest req, HttpServletResponse resp, @RequestBody String jsonreq) throws Exception {
        Map<String,Object> requestmap = new HashMap <> ();      //接收请求的map
        Map<String,Object> tradeParamMap = new HashMap <> ();   //请求中tradeParam部分
        Map<String,Object> datamap = new HashMap <> ();         //返回替中响应datamap
        Map<String,Object> reqdbmap = new HashMap <> ();        //请求dbserver
        Map<String,Object> respdbmap = new HashMap <> ();       //dbserver返回
        logger.info ("getOutThirdData接收请求数据 = {}", jsonreq);


        requestmap = JsonUtils.JsonToMapObj (jsonreq);
        tradeParamMap = (Map <String, Object>) requestmap.get ("tradeParamMap");
        String sign = (String) requestmap.get ("sign");

        //----------------step01 tradeType校验-------------
        if (!PayUtils.vertifytradType(datamap,"Out_ThirdData")) {
           throw new CenterErrorException (ResponseMsgEnum.TRADETYPE_EXCEPTION);    //tradeType错误
        }

        //----------------step02 sign校验-------------------
        if (!PayUtils.vertifysign (tradeParamMap,sign)) {
            throw new CenterErrorException (ResponseMsgEnum.SIGN_EXCEPTION);        //sign错误
        }

        //----------------step03 tradeParam参数校验----------
        List<String> keyList = new ArrayList<String> ();
        keyList.add ("chanelType");
        keyList.add ("merchantId");
        keyList.add ("accDate");
        keyList.add ("thirdId");
        keyList.add ("orderId");
        keyList.add ("orderType");
        keyList.add ("transtype");
        keyList.add ("paynum");
        PayUtils.vertifyParam (tradeParamMap,keyList);   //若请求参数存在问题，会抛出异常，并返回给前端异常信息

        if ("-1".equals (tradeParamMap.get ("transtype"))) {    //退款交易,验证退款交易必传项
            List<String> keyList2 = new ArrayList<String> ();
            keyList2.add ("refundAmt");
            keyList2.add ("refundOrderId");
            keyList2.add ("refundReason");
            PayUtils.vertifyParam (tradeParamMap,keyList);
        }

        //----------------step04 数据存入t_third_trans表中------------
        reqdbmap = tradeParamMap;
        respdbmap = ggpayService.insertThirdTrans (reqdbmap);
        Map<String,Object> conresult = (Map <String, Object>) respdbmap.get ("conResult");
        if (!respdbmap.get("statuscode").equals("200") || !conresult.get("rspCode").equals("00")) {
            throw new CenterErrorException (ResponseMsgEnum.REQUEST_RESP_EXCEPTION,"第三方账单录入失败！");
        }else {
            return ResponseUtils.success ("第三方账单录入成功！");
        }

    }

    /**
     * 获取外部HIS账单数据
     * @param req
     * @param resp
     * @param jsonreq
     * @return
     * @throws Exception
     */
    @RequestMapping("/getOutHisData")
    public ResponseInfo getOutHisBillData(HttpServletRequest req, HttpServletResponse resp, @RequestBody String jsonreq) throws Exception {
        Map<String,Object> requestmap = new HashMap <> ();          //接收请求的map
        Map<String,Object> tradeParamMap = new HashMap <> ();       //请求中tradeParam部分
        Map<String,Object> datamap  = new HashMap <> ();            //返回替中响应datamap
        Map<String,Object> reqdbmap = new HashMap <> ();            //请求dbserver
        Map<String,Object> respdbmap = new HashMap <> ();       //dbserver返回
        logger.info ("getOutThirdData接收请求数据 = {}", jsonreq);


        requestmap = JsonUtils.JsonToMapObj (jsonreq);
        tradeParamMap = (Map <String, Object>) requestmap.get ("tradeParamMap");
        String sign = (String) requestmap.get ("sign");

        //----------------step01 tradeType校验-------------
        if (!PayUtils.vertifytradType(datamap,"Out_ThirdData")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADETYPE_EXCEPTION);    //tradeType错误
        }

        //----------------step02 sign校验-------------------
        if (!PayUtils.vertifysign (tradeParamMap,sign)) {
            throw new CenterErrorException (ResponseMsgEnum.SIGN_EXCEPTION);        //sign错误
        }

        //----------------step03 tradeParam参数校验----------
        List keyList = new ArrayList ();
        keyList.add ("merchantId");
        keyList.add ("hisTransDate");
        keyList.add ("orderId");
        keyList.add ("orderType");
        keyList.add ("transtype");
        PayUtils.vertifyParam (tradeParamMap,keyList);                   //若请求参数存在问题，会抛出异常，以json格式返回异常信息

        if ("-1".equals (tradeParamMap.get ("transtype"))) {    //退款交易,验证退款交易必传项
            List keyList2 = new ArrayList ();
            keyList2.add ("refundAmt");
            keyList2.add ("refundOrderId");
            keyList2.add ("refundReason");
            PayUtils.vertifyParam (tradeParamMap,keyList);
        }

        //----------------step04 数据存入t_his_trans表中-------------------
        reqdbmap = tradeParamMap;
        respdbmap = ggpayService.insertHisTrans (reqdbmap);
        Map<String,Object> conresult = (Map <String, Object>) respdbmap.get ("conResult");
        if (!respdbmap.get("statuscode").equals("200") || !conresult.get("rspCode").equals("00")) {
            throw new CenterErrorException (ResponseMsgEnum.REQUEST_RESP_EXCEPTION,"HIS账单录入失败！");
        }else {
            return ResponseUtils.success ("HIS账单录入成功！");
        }
    }

}
