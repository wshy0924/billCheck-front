package com.wshy.billcheck.controller;


import com.wshy.billcheck.Exception.CenterErrorException;
import com.wshy.billcheck.Exception.ResponseInfo;
import com.wshy.billcheck.Exception.ResponseMsgEnum;
import com.wshy.billcheck.Utils.JsonUtils;
import com.wshy.billcheck.Utils.PayUtils;
import com.wshy.billcheck.Utils.ResponseUtils;
import com.wshy.billcheck.serviceImpl.AliPayServiceImpl;
import com.wshy.billcheck.serviceImpl.WxPayServiceImpl;
import com.wshy.billcheck.serviceImpl.dbServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/7/1
 **/
@RequestMapping(value = "/PayPlatform",method = RequestMethod.POST)
public class PayPlatformController {
    private final static Logger logger = LoggerFactory.getLogger (PayPlatformController.class);

    @Autowired
    private AliPayServiceImpl aliPayService;
    @Autowired
    private WxPayServiceImpl wxPayService;
    @Autowired
    private dbServiceImpl dbService;

    /**
     * 创建订单（用户扫商户二维码）
     * 适用的支付类型0102、0103、0202、0203、0302、0401、0702、0703、0704
     * 平台返回支付连接，前端接收链接，并转化成支付二维码，用户扫描二维码完成支付
     * @param request
     * @param response
     * @param jsonparam
     * @return
     * @throws CenterErrorException
     */
    @RequestMapping("/createOrder")
    public ResponseInfo createOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonparam) throws Exception {
        logger.info ("------------------PayPlatform createOrder------------------------");
        logger.info ("PayPlatform jsonparam = " + jsonparam);
        Map<String,Object> reqMap = new HashMap <> ();  //前端jsonparam转换后的map
        Map<String,Object> tradeParamMap = new HashMap <> ();   //tradeParam部分map
        Map<String, Object> resp = new HashMap<String, Object>();// 与其他服务交互返回
        Map<String, Object> respdata = new HashMap<String, Object>();// 与其他服务交互返回结果的子集
        Map<String,Object> datamap = new HashMap <> (); // 数据库服务返回data部分map
        List<Map> datalist = new ArrayList <> (); // 数据库服务返回data部分list
        Map<String, Object> resultmap = new HashMap<String, Object>();// 接口返回的map

        reqMap = JsonUtils.JsonToMapObj (jsonparam);
        tradeParamMap = (Map <String, Object>) reqMap.get ("tradeParam");
        logger.info ("PayPlatform tradeParamMap = " + tradeParamMap);

        String sign = reqMap.get ("sign").toString ();
        String tradeType = reqMap.get ("tradeType").toString ();

        //----------------------step01 tradeType校验--------------------
        if (!PayUtils.vertifytradType (reqMap,"0001")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADETYPE_EXCEPTION);
        }

        //------------------------Step02 sign校验-----------------------
        if (!PayUtils.vertifysign (tradeParamMap,sign)) {
            throw new CenterErrorException (ResponseMsgEnum.SIGN_EXCEPTION);
        }

        //------------------------step03 渠道、商户、终端号、操作员是否合法--------------
        if (!PayUtils.verifyParameter(tradeParamMap)) {
            throw new CenterErrorException (ResponseMsgEnum.PARMETER_EXCEPTION);
        }

        //------------------------step04 验证支付类型与交易号是否匹配--------------------
        if (!("0702".equals(tradeParamMap.get("payType")) || "0401".equals(tradeParamMap.get("payType"))
                || "0302".equals(tradeParamMap.get("payType")) || "0202".equals(tradeParamMap.get("payType"))
                || "0102".equals(tradeParamMap.get("payType")) || "0703".equals(tradeParamMap.get("payType"))
                || "0704".equals(tradeParamMap.get("payType")) || "0103".equals(tradeParamMap.get("payType"))
                || "0203".equals(tradeParamMap.get("payType")))) {

            throw new CenterErrorException (ResponseMsgEnum.PAYTYPE_EXCEPTION);
        }

        //----------------------step05 请求数据插入t_order表---------------------------
        tradeParamMap.put ("fOrdertrace",PayUtils.generatePipeline());  //生成平台订单号
        tradeParamMap.put("fRegdate", tradeParamMap.get("fOrdertrace").toString().substring(0, 8)); //订单日期
        tradeParamMap.put("fRegtime", tradeParamMap.get("fOrdertrace").toString().substring(8, 14));//订单具体时间

        resp = dbService.createOrder (tradeParamMap);
        logger.info("createOrder t_order resp = " + resp);
        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        //resp返回码为“0000”时成功，statuscode为http请求返回状态码
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("insertflag")) {
           throw new CenterErrorException (ResponseMsgEnum.TRADE_CREATEORDER_EXCEPTION);    //订单创建失败
        } else {
            tradeParamMap.put("fProduct", resp.get("fProduct"));// 后面步骤有的需要，数据库中取订单标题，使支付宝微信订单标题一致
        }

        //---------------------------step06 数据库t_oder_pay表创建订单--------------------
        resp = dbService.createOrderpay (tradeParamMap);
        respdata = (Map <String, Object>) resp.get ("conResult");
        datamap = (Map <String, Object>) respdata.get ("data");
        logger.info ("createOrder t_oder_pay resp = " + resp);
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("insertflag")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_CREATEORDER_EXCEPTION);   //订单创建失败
        }

        //-----------------------------step07 与支付包、微信、银联进行通信-----------------
        resultmap.put ("orderId",tradeParamMap.get ("orderId"));
        if ("0102".equals(tradeParamMap.get("payType").toString())) {// 支付宝商户被扫
            logger.info("------------- createOrder 支付宝 ------------- ");
            resp = aliPayService.f2fScanQrCodePay(tradeParamMap);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
               throw new CenterErrorException (ResponseMsgEnum.TRADE_FAILTOGETQRCODE_EXCEPTION);    //获取二维码失败
            } else {
                logger.info("------------- QrCodeData ------------- " + respdata.get("QrCodeData"));
                resultmap.put("qrUrl", respdata.get("QrCodeData"));
            }
        } else if ("0202".equals(tradeParamMap.get("payType").toString())) {// 微信商户被扫
            logger.info("------------- createOrder 微信 --------------- ");
            resp = wxPayService.unifiedPayforNATIVE(tradeParamMap);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_FAILTOGETQRCODE_EXCEPTION);
            } else {
                logger.info("------------- QrCodeData ------------- " + respdata.get("QrCodeData"));
                resultmap.put("qrUrl", respdata.get("code_url"));
            }
        } else if ("0203".equals(tradeParamMap.get("payType").toString())) {// 微信公众号,需先获取
            logger.info("------------- createOrder 微信公众号 --------------- ");
            resp = wxPayService.unifiedPay(tradeParamMap);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_CREATEORDER_EXCEPTION);   //创建订单失败
            } else {
                logger.info("------------- 微信公众号prepay_id ------------- " + respdata.get("prepay_id"));
                resultmap.put("thirdCode", respdata.get("prepay_id"));
            }
        }else if ("0302".equals(tradeParamMap.get("payType").toString())) {// 银联

        } else if ("0401".equals(tradeParamMap.get("payType").toString())) {// epos

        } else if ("0702".equals(tradeParamMap.get("payType").toString())) {// 工行聚合支付

        } else if ("0703".equals(tradeParamMap.get("payType").toString())) {//工行聚合支付支付宝生活号

        } else if ("0704".equals(tradeParamMap.get("payType").toString())) {//工行聚合支付微信公众号

        }

        return ResponseUtils.success (resultmap);
    }

    /**
     * 订单支付（商户扫描用户支付码）
     * 适用的支付类型：0101、0201、0301、0501、0601、0701
     * 直接返回状态码和平台订单流水号
     * @param request
     * @param response
     * @param jsonparam
     * @return
     * @throws Exception
     */
    @RequestMapping("/payOrder")
    public ResponseInfo payOrder (HttpServletRequest request,HttpServletResponse response,@RequestBody String jsonparam) throws Exception {

        logger.info ("------------------PayPlatform payOrder------------------------");
        logger.info ("PayPlatform jsonparam = " + jsonparam);
        Map<String,Object> reqMap = new HashMap <> ();  //前端jsonparam转换后的map
        Map<String,Object> tradeParamMap = new HashMap <> ();   //tradeParam部分map
        Map<String, Object> resp = new HashMap<String, Object>();// 与其他服务交互返回
        Map<String, Object> respdata = new HashMap<String, Object>();// 与其他服务交互返回结果的子集
        Map<String,Object> datamap = new HashMap <> (); // 数据库服务返回data部分map
        List<Map> datalist = new ArrayList <> (); // 数据库服务返回data部分list
        Map<String, Object> resultmap = new HashMap<String, Object>();// 接口返回的map

        reqMap = JsonUtils.JsonToMapObj (jsonparam);
        tradeParamMap = (Map <String, Object>) reqMap.get ("tradeParam");
        logger.info ("PayPlatform tradeParamMap = " + tradeParamMap);

        String sign = reqMap.get ("sign").toString ();
        String tradeType = reqMap.get ("tradeType").toString ();

        try {
            //----------------------step01 tradeType校验--------------------
            if (!PayUtils.vertifytradType (reqMap,"0001")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADETYPE_EXCEPTION);
            }

            //------------------------Step02 sign校验-----------------------
            if (!PayUtils.vertifysign (tradeParamMap,sign)) {
                throw new CenterErrorException (ResponseMsgEnum.SIGN_EXCEPTION);
            }

            //------------------------step03 渠道、商户、终端号、操作员是否合法--------------
            if (!PayUtils.verifyParameter(tradeParamMap)) {
                throw new CenterErrorException (ResponseMsgEnum.PARMETER_EXCEPTION);
            }

            //------------------------step04 验证支付类型与交易号是否匹配--------------------
            if (!("0101".equals(tradeParamMap.get("payType")) || "0201".equals(tradeParamMap.get("payType"))
                    || "0301".equals(tradeParamMap.get("payType")) || "0501".equals(tradeParamMap.get("payType"))
                    || "0601".equals(tradeParamMap.get("payType")) || "0701".equals(tradeParamMap.get("payType")))) {

                throw new CenterErrorException (ResponseMsgEnum.PAYTYPE_EXCEPTION);
            }

            //----------------------step05 请求数据插入t_order表---------------------------
            tradeParamMap.put ("fOrdertrace",PayUtils.generatePipeline());
            tradeParamMap.put("fRegdate", tradeParamMap.get("fOrdertrace").toString().substring(0, 8));
            tradeParamMap.put("fRegtime", tradeParamMap.get("fOrdertrace").toString().substring(8, 14));

            resp = dbService.createOrder (tradeParamMap);
            logger.info("payOrder oder_pay resp = " + resp);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            datamap = (Map <String, Object>) respdata.get ("data");
            //resp返回码为“00”时成功，statuscode为http请求返回状态码
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("insertflag")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_CREATEORDER_EXCEPTION);    //订单创建失败
            } else {
                tradeParamMap.put("fProduct", resp.get("fProduct"));// 后面步骤有的需要，数据库中取订单标题，使支付宝微信订单标题一致
            }

            //---------------------------step06 数据库t_order_pay表创建订单--------------------
            resp = dbService.createOrderpay (tradeParamMap);
            datamap = (Map <String, Object>) respdata.get ("data");
            logger.info ("payOrder t_oder_pay resp = " + resp);
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("insertflag")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_CREATEORDER_EXCEPTION);   //订单创建失败
            }

            //-----------------------------step07 与支付宝、微信、银联进行通信-----------------
            resultmap.put ("orderId",tradeParamMap.get ("orderId"));    //返回的第三方交易号

            if ("0101".equals(tradeParamMap.get("payType").toString())) {   //支付宝商户扫描用于二维码
                logger.info("------------- payOrder 支付宝 ------------- ");
                resp = aliPayService.f2fBarCodePay(tradeParamMap);
                respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
                if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                    throw new CenterErrorException (ResponseMsgEnum.TRADE_PAYORDER_EXCEPTION);    //订单支付失败
                }
            } else if ("0201".equals(tradeParamMap.get("payType").toString())) {    //微信商户扫描用户二维码
                logger.info("------------- payOrder 微信 --------------- ");
                resp = wxPayService.microPay(tradeParamMap);
                respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
                if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                    throw new CenterErrorException (ResponseMsgEnum.TRADE_PAYORDER_EXCEPTION);     //订单支付失败
                }
            } else if ("0301".equals(tradeParamMap.get("payType").toString())) {    //银联
                logger.info("------------- payOrder 银联支付支付 --------------- ");
            } else if ("0501".equals(tradeParamMap.get("payType").toString())) {    //现金
                logger.info("------------- payOrder 现金支付 --------------- ");
            } else if ("0601".equals(tradeParamMap.get("payType").toString())) {    //社保卡
                logger.info("------------- payOrder 社保卡支付 --------------- ");
            } else if ("0701".equals(tradeParamMap.get("payType").toString())) {    //工行聚合支付
                logger.info("------------- payOrder 工行聚合支付 --------------- ");
            }
        } catch (Exception e) {
            e.printStackTrace ();
            return ResponseUtils.failure (ResponseMsgEnum.INNORMAL_RETURN,"系统异常！");   //系统异常
        }

        return ResponseUtils.success (resultmap);

    }

    /**
     * 交易查询
     * 适用渠道：自助、人工窗口、微信公众号、支付宝窗口号
     * 适用支付类型：所有支付类型
     * @param request
     * @param response
     * @param jsonparam
     * @return
     * @throws Exception
     */
    @RequestMapping("/queryOrder")
    public ResponseInfo queryOrder(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonparam) throws Exception {
        logger.info ("------------------PayPlatform queryOrder------------------------");
        logger.info ("PayPlatform jsonparam = " + jsonparam);
        Map<String,Object> reqMap = new HashMap <> ();  //前端jsonparam转换后的map
        Map<String,Object> tradeParamMap = new HashMap <> ();   //tradeParam部分map
        Map<String, Object> resp = new HashMap<String, Object>();// 与其他服务交互返回
        Map<String, Object> respdata = new HashMap<String, Object>();// 与其他服务交互返回结果的子集
        Map<String,Object> datamap = new HashMap <> (); // 数据库服务返回data部分map
        List <Map <String, Object>> datalist = new ArrayList <> (); // 数据库服务返回data部分list
        Map<String, Object> resultmap = new HashMap<String, Object>();// 接口返回的map

        reqMap = JsonUtils.JsonToMapObj (jsonparam);
        tradeParamMap = (Map <String, Object>) reqMap.get ("tradeParam");
        logger.info ("PayPlatform tradeParamMap = " + tradeParamMap);

        String sign = reqMap.get ("sign").toString ();
        String tradeType = reqMap.get ("tradeType").toString ();

        //------------------------step01 tradeType校验--------------------
        if (!PayUtils.vertifytradType (reqMap,"0003")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADETYPE_EXCEPTION);
        }

        //------------------------Step02 sign校验-----------------------
        if (!PayUtils.vertifysign (tradeParamMap,sign)) {
            throw new CenterErrorException (ResponseMsgEnum.SIGN_EXCEPTION);
        }

        //------------------------step03 渠道、商户、终端号、操作员是否合法--------------
        if (!PayUtils.verifyParameter(tradeParamMap)) {
            throw new CenterErrorException (ResponseMsgEnum.PARMETER_EXCEPTION);
        }

        //------------------------step04 数据库t_order查询支付类型--------------------
        resp = dbService.queryOrderinfo(tradeParamMap);
        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYORDER_EXCEPTION);    //订单查询失败
        } else {
            logger.info("queryOrder queryOrderinfo respdata = " + respdata);
            datalist = (List <Map <String, Object>>) datamap.get("dataList");                          //数据库返回data部分
            Map<String, Object> dbdatamap = datalist.get(0);  //根据订单号查询结果唯一
            tradeParamMap.put("payType", dbdatamap.get("fPaytype"));                       //支付类型
        }

        //------------------------step05 与支付宝、微信、银联通信，根据paytype判断使用何种支付方式进行订单结果查询--------------
        if ("0101".equals (tradeParamMap.get ("payType")) || "0102".equals (tradeParamMap.get ("payType"))) {   //支付宝
            logger.info("------------- queryOrder 支付宝-------------- ");
            resp = aliPayService.f2fQueryPayResultForLoop(tradeParamMap);                                       //调用支付宝轮询接口
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYORDERFAIL_EXCEPTION);                //支付结果查询失败，请继续查询
            } else {
                Map<String, Object> data = (Map<String, Object>) respdata.get("data");
                if (!"Payed".equals(data.get("status"))) {
                   throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYORDERUNPAYED_EXCEPTION);          //订单未支付
                } else {// 订单查询,成功
                    tradeParamMap.put("f3openid", data.get("buyer"));                                           // 付款人第三方id
                    tradeParamMap.put("f3transaction_id", data.get("alipayTradeNo"));                           // 第三方流水号

                    // datamap.put("f3date_end", "");//null就不更新这个字段
                    // datamap.put("f3time_end", "");//null就不更新这个字段
                }
            }
        } else if ("0201".equals(tradeParamMap.get("payType").toString()) || "0202".equals(tradeParamMap.get("payType").toString())) {                                    // 微信
            logger.info("------------- queryOrder 微信 --------------- ");
            resp = wxPayService.orderQuery(tradeParamMap);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!"200".equals(resp.get("statuscode")) || !"0000".equals(respdata.get("returnCode"))) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYORDERFAIL_EXCEPTION);                //支付结果查询失败，请继续查询
            } else {
                Map<String, Object> data = (Map<String, Object>) respdata.get("data");
                if ("SUCCESS".equals(data.get("trade_state"))) {
                    tradeParamMap.put("f3transaction_id", data.get("transaction_id"));                           //第三方流水号

                } else {
                    throw new CenterErrorException (ResponseMsgEnum.TRADE_PAYORDER_EXCEPTION);                   //订单支付失败
                }
            }
        }else if ("0103".equals(tradeParamMap.get("payType").toString()) || "0203".equals(tradeParamMap.get("payType").toString())) {//支付宝窗口号和微信公众号

        } else if ("0301".equals(tradeParamMap.get("payType").toString()) || "0302".equals(tradeParamMap.get("payType").toString())) {
            logger.info("------------- queryOrder 银联 --------- ");
        } else if ("04".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// epos,第三方流水由终端上送
            logger.info("------------- queryOrder pos --------- ");
        } else if ("05".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 现金
            logger.info("------------- queryOrder 现金 --------- ");
        } else if ("06".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 医保
            logger.info("------------- queryOrder 医保 --------- ");
        } else if ("0701".equals(tradeParamMap.get("payType").toString()) || "0702".equals(tradeParamMap.get("payType").toString())) {// 工行聚合支付扫码
            logger.info("------------- queryOrder 工行聚合支付扫码 --------- ");

        } else if ("0703".equals(tradeParamMap.get("payType").toString()) || "0704".equals(tradeParamMap.get("payType").toString())) {// 工行聚合支付公众号生活号
            logger.info("------------- queryOrder 工行聚合支付公众号生活号 --------- ");
        }

        tradeParamMap.put("order_status", "3");// order表状态，3表示已支付
        tradeParamMap.put("orderpay_status", "1");// orderpay表状态，1表示已确认

        //-----------------------------------step6 数据库t_order表更新状态------------------------
        logger.info ("------------------数据库t_order表更新状态----------------------");
        resp = dbService.updateOrder(tradeParamMap);

        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("updateflag")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYORDER_EXCEPTION);    //订单查询失败
        }

        //-----------------------------------step7 数据库t_order_pay表更新状态------------------------
        logger.info ("------------------数据库t_order_pay表更新状态----------------------");
        resp = dbService.updateOrderpay(tradeParamMap);
        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("updateflag")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYORDER_EXCEPTION);    //订单查询失败
        }

        resultmap.put("orderId", tradeParamMap.get("orderId")); //返回订单号
        return ResponseUtils.success (resultmap);
    }

    /**
     *订单撤销
     * 适用渠道：自助、人工窗口、微信公众号、支付宝窗口号
     * 适用支付类型：0101、0102、0103、0201、0202、0203、0301、0302
     * @param request
     * @param response
     * @param jsonparam
     * @return
     * @throws Exception
     */
    @RequestMapping("/revokeOrder")
    public ResponseInfo revokeOrder(HttpServletRequest request,HttpServletResponse response,@RequestBody String jsonparam) throws Exception {
        logger.info ("------------------PayPlatform queryOrder------------------------");
        logger.info ("PayPlatform jsonparam = " + jsonparam);
        Map<String,Object> reqMap = new HashMap <> ();  //前端jsonparam转换后的map
        Map<String,Object> tradeParamMap = new HashMap <> ();   //tradeParam部分map
        Map<String, Object> resp = new HashMap<String, Object>();// 与其他服务交互返回
        Map<String, Object> respdata = new HashMap<String, Object>();// 与其他服务交互返回结果的子集
        Map<String,Object> datamap = new HashMap <> (); // 数据库服务返回data部分map
        List <Map <String, Object>> datalist = new ArrayList <Map <String, Object>> (); // 数据库服务返回data部分list
        Map<String, Object> resultmap = new HashMap<String, Object>();// 接口返回的map

        reqMap = JsonUtils.JsonToMapObj (jsonparam);
        tradeParamMap = (Map <String, Object>) reqMap.get ("tradeParam");
        logger.info ("PayPlatform tradeParamMap = " + tradeParamMap);

        String sign = reqMap.get ("sign").toString ();
        String tradeType = reqMap.get ("tradeType").toString ();

        //------------------------step01 tradeType校验--------------------
        if (!PayUtils.vertifytradType (reqMap,"0004")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADETYPE_EXCEPTION);
        }

        //------------------------Step02 sign校验-----------------------
        if (!PayUtils.vertifysign (tradeParamMap,sign)) {
            throw new CenterErrorException (ResponseMsgEnum.SIGN_EXCEPTION);
        }

        //------------------------step03 渠道、商户、终端号、操作员是否合法--------------
        if (!PayUtils.verifyParameter(tradeParamMap)) {
            throw new CenterErrorException (ResponseMsgEnum.PARMETER_EXCEPTION);
        }

        //------------------------step04 验证支付类型与交易号是否匹配--------------------
//        if (!("0101".equals(tradeParamMap.get("payType")) || "0102".equals(tradeParamMap.get("payType"))
//                || "0103".equals(tradeParamMap.get("payType")) || "0201".equals(tradeParamMap.get("payType"))
//                || "0202".equals(tradeParamMap.get("payType")) || "0203".equals(tradeParamMap.get("payType"))
//                || "0301".equals(tradeParamMap.get("payType")) || "0302".equals(tradeParamMap.get("payType")))) {
//            throw new CenterErrorException (ResponseMsgEnum.PAYTYPE_EXCEPTION);
//        }

        //------------------------step04 数据库t_order查询支付类型--------------------
        resp = dbService.queryOrderinfo(tradeParamMap); //根据订单流水号查询t_order表
        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") ) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_REVOKEORDER_EXCEPTION);   //订单取消失败
        } else {
            logger.info("revokeOrder queryOrderinfo subresp = " + respdata);
            datalist = (ArrayList <Map <String, Object>>) datamap.get("dataList");
            Map<String, Object> data = datalist.get(0);   //根据订单号查询结果唯一
            tradeParamMap.put("payType", data.get("fPaytype"));// 支付类型匹配
            // datamap.put("ybThirdid", orderinfomap.get("f_ybthirdid"));// 医保自费使用的第三方支付ID
        }

        //------------------------step05 与支付宝、微信、银联通信，根据paytype判断使用何种支付方式进行订单结果查询--------------
        if ("01".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 支付宝
            logger.info("------------- revokeOrder 支付宝-------------- ");
            resp = aliPayService.f2fCancelPay(tradeParamMap);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_REVOKEORDER_EXCEPTION);   //订单取消失败
            } else {
                tradeParamMap.put("order_status", "4");// order表状态，4表示已关闭
                tradeParamMap.put("orderpay_status", "V");// orderpay表状态，V表示已关闭
            }
        } else if ("02".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 微信
            logger.info("------------- revokeOrder 微信-------------- ");
            resp = wxPayService.closeOrder(tradeParamMap);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_REVOKEORDER_EXCEPTION);   //订单取消失败
            } else {
                tradeParamMap.put("order_status", "4");// order表状态，4表示已关闭
                tradeParamMap.put("orderpay_status", "V");// orderpay表状态，V表示已关闭
            }
        } else if ("03".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 银联
            logger.info("------------- revokeOrder 银联-------------- ");
        }

        //-------------------step7 数据库t_order表更新状态----------------
        logger.info ("------------------数据库t_order表更新状态----------------------");
        resp = dbService.updateOrder(tradeParamMap);

        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("updateflag")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYORDER_EXCEPTION);    //订单查询失败
        }

        //-------------------step8数据库t_oder_pay表更新状态-------------
        logger.info ("------------------数据库t_order_pay表更新状态----------------------");
        resp = dbService.updateOrderpay(tradeParamMap);
        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");

        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("updataflag")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_REVOKEORDER_EXCEPTION);    //订单撤销失败
        }
        resultmap.put("orderId", tradeParamMap.get("orderId"));

        return ResponseUtils.success (resultmap);
    }

    /**
     * 订单退款
     * 适用渠道：自助、人工窗口、微信公众号、支付宝窗口号
     * 适用支付类型：所有支付类型
     * @param request
     * @param response
     * @param jsonparam
     * @return
     * @throws Exception
     */
    @RequestMapping("/refundOrder")
    public ResponseInfo refundOrder(HttpServletRequest request,HttpServletResponse response,@RequestBody String jsonparam) throws Exception {
        logger.info ("------------------PayPlatform refundOrder------------------------");
        logger.info ("PayPlatform jsonparam = " + jsonparam);
        Map<String,Object> reqMap = new HashMap <> ();          //前端jsonparam转换后的map
        Map<String,Object> tradeParamMap = new HashMap <> ();   //tradeParam部分map
        Map<String, Object> resp = new HashMap<String, Object>();            //与其他服务交互返回
        Map<String, Object> respdata = new HashMap<String, Object>();        //与数据库服务交互返回结果的data集合部分
        Map<String,Object> datamap = new HashMap <> (); // 数据库服务返回data部分map
        List<Map> datalist = new ArrayList <> (); // 数据库服务返回data部分list
        Map<String, Object> resultmap = new HashMap<String, Object>();       //接口返回的map

        reqMap = JsonUtils.JsonToMapObj (jsonparam);
        tradeParamMap = (Map <String, Object>) reqMap.get ("tradeParam");
        logger.info ("PayPlatform tradeParamMap = " + tradeParamMap);

        String sign = reqMap.get ("sign").toString ();
        String tradeType = reqMap.get ("tradeType").toString ();

        //------------------------step01 tradeType校验--------------------
        if (!PayUtils.vertifytradType (reqMap,"0005")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADETYPE_EXCEPTION);
        }

        //------------------------Step02 sign校验-----------------------
        if (!PayUtils.vertifysign (tradeParamMap,sign)) {
            throw new CenterErrorException (ResponseMsgEnum.SIGN_EXCEPTION);
        }

        //------------------------step03 渠道、商户、终端号、操作员是否合法--------------
        if (!PayUtils.verifyParameter(tradeParamMap)) {
            throw new CenterErrorException (ResponseMsgEnum.PARMETER_EXCEPTION);
        }

        //------------------------step04 验证支付类型与交易号是否匹配--------------------
//        if (!("0101".equals(tradeParamMap.get("payType")) || "0102".equals(tradeParamMap.get("payType"))
//                || "0103".equals(tradeParamMap.get("payType")) || "0201".equals(tradeParamMap.get("payType"))
//                || "0202".equals(tradeParamMap.get("payType")) || "0203".equals(tradeParamMap.get("payType"))
//                || "0301".equals(tradeParamMap.get("payType")) || "0302".equals(tradeParamMap.get("payType")))) {
//            throw new CenterErrorException (ResponseMsgEnum.PAYTYPE_EXCEPTION);
//        }

        //------------------------step04 数据库t_order查询支付类型--------------------
        resp = dbService.queryOrderinfo(tradeParamMap);
        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_REFUNDORDER_EXCEPTION);   //订单退款失败
        } else {
            logger.info("refundOrder queryOrderinfo subresp = " + respdata);
            datalist = (ArrayList) datamap.get("dataList");
            Map<String, Object> data = (Map<String, Object>) datalist.get(0);
            tradeParamMap.put("payType", data.get("fPaytype"));// 支付类型匹配
            // datamap.put("ybThirdid", orderinfomap.get("f_ybthirdid"));// 医保自费使用的第三方支付ID
        }

        //------------------------step05 数据库t_order_pay查询f_index,判断退款次数--------------------
        resp = dbService.queryOrderpayindex(tradeParamMap);

        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        datalist = (List <Map>) datamap.get ("dataList");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_REFUNDORDER_EXCEPTION);   //退款失败
        } else {
            // datamap.put("payType", orderinfomap.get("f_paytype"));//支付类型匹配
            int indexvalue = new Integer((String) datalist.get (0).get("fIndex")).intValue();   //查询结果唯一
            if (indexvalue != 1) {  //每笔订单只支持一次退款，不等于1，表示订单已经退款
               throw new CenterErrorException (ResponseMsgEnum.TRADE_REFUNDORDER_EXCEPTION);    //退款失败
            } else {
                indexvalue++;
                tradeParamMap.put("f_index", indexvalue + "");
            }
        }

        //------------------------step06 与支付宝、微信、银联通信，根据paytype判断使用何种支付方式进行订单结果查询--------------
        if ("01".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 支付宝
            logger.info("------------- refundOrder 支付宝-------------- ");
            resp = aliPayService.f2fRefundTradePay(tradeParamMap);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_REFUNDORDER_EXCEPTION);    //退款失败
            }
        } else if ("02".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 微信
            logger.info("------------- refundOrder 微信 --------------- ");
            resp = wxPayService.orderRefund(tradeParamMap);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_REFUNDORDER_EXCEPTION);    //退款失败
            }
        } else if ("04".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// epos--不做退费
            logger.info("------------- refundOrder epos --------------- ");
        } else if ("05".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 现金--不做退费
            logger.info("------------- refundOrder 现金 --------------- ");
        } else if ("06".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 医保--不做退费
            logger.info("------------- refundOrder 医保 --------------- ");
        } else if ("07".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 工行聚合支付
            logger.info("------------- refundOrder 工行聚合支付 --------- ");
        }

        //------------------------step08 数据库t_order_pay创建订单退款记录-------------
        String refundOrdertrace = "";
        if (tradeParamMap.get("outrefundorderId") != null && tradeParamMap.get("outrefundorderId").toString().length() == 20) {
            refundOrdertrace = tradeParamMap.get("outrefundorderId").toString();
        } else {
            refundOrdertrace = PayUtils.generatePipeline();
        }

        tradeParamMap.put("fOrdertrace", refundOrdertrace);// !!!!!退款交易新生成专用流水!!!!!!
        tradeParamMap.put("fRegdate", tradeParamMap.get("fOrdertrace").toString().substring(0, 8));
        tradeParamMap.put("fRegtime", tradeParamMap.get("fOrdertrace").toString().substring(8, 14));
        resp = dbService.createOrderpayrefund(tradeParamMap);// 生成订单支付记录(退费)

        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("insertflag")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_REFUNDORDER_EXCEPTION);    //退款失败
        }

        resultmap.put("orderId", tradeParamMap.get("orderId"));// 支付时的订单号
        resultmap.put("refundorderId", tradeParamMap.get("fOrdertrace"));// 退费时的订单号,退费查询时候需要上送
        resultmap.put("refundAmt", tradeParamMap.get("refundAmt"));
        resultmap.put("index", tradeParamMap.get("f_index"));// 第几次退款

        return ResponseUtils.success (resultmap);
    }

    /**
     * 退款查询
     * 适用渠道：自助、人工窗口、微信公众号、支付宝窗口号
     * 适用支付类型：所有支付类型
     * @param request
     * @param response
     * @param jsonparam
     * @return
     * @throws Exception
     */
    public ResponseInfo queryRefund(HttpServletRequest request,HttpServletResponse response,@RequestBody String jsonparam) throws Exception {
        logger.info ("------------------PayPlatform queryRefund------------------------");
        logger.info ("PayPlatform jsonparam = " + jsonparam);
        Map<String,Object> reqMap = new HashMap <> ();          //前端jsonparam转换后的map
        Map<String,Object> tradeParamMap = new HashMap <> ();   //tradeParam部分map
        Map<String, Object> resp = new HashMap<String, Object>();            //与其他服务交互返回
        Map<String, Object> respdata = new HashMap<String, Object>();        //与数据库服务交互返回结果的data集合部分
        Map<String,Object> datamap = new HashMap <> (); // 数据库服务返回data部分map
        List <Map <String, Object>> datalist = new ArrayList <> (); // 数据库服务返回data部分list
        Map<String, Object> resultmap = new HashMap<String, Object>();       //接口返回的map

        reqMap = JsonUtils.JsonToMapObj (jsonparam);
        tradeParamMap = (Map <String, Object>) reqMap.get ("tradeParam");
        logger.info ("PayPlatform tradeParamMap = " + tradeParamMap);

        String sign = reqMap.get ("sign").toString ();
        String tradeType = reqMap.get ("tradeType").toString ();

        //------------------------step01 tradeType校验--------------------
        if (!PayUtils.vertifytradType (reqMap,"0005")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADETYPE_EXCEPTION);
        }

        //------------------------Step02 sign校验-----------------------
        if (!PayUtils.vertifysign (tradeParamMap,sign)) {
            throw new CenterErrorException (ResponseMsgEnum.SIGN_EXCEPTION);
        }

        //------------------------step03 渠道、商户、终端号、操作员是否合法--------------
        if (!PayUtils.verifyParameter(tradeParamMap)) {
            throw new CenterErrorException (ResponseMsgEnum.PARMETER_EXCEPTION);
        }

        //------------------------step04 验证支付类型与交易号是否匹配--------------------
//        if (!("0101".equals(tradeParamMap.get("payType")) || "0102".equals(tradeParamMap.get("payType"))
//                || "0103".equals(tradeParamMap.get("payType")) || "0201".equals(tradeParamMap.get("payType"))
//                || "0202".equals(tradeParamMap.get("payType")) || "0203".equals(tradeParamMap.get("payType"))
//                || "0301".equals(tradeParamMap.get("payType")) || "0302".equals(tradeParamMap.get("payType")))) {
//            throw new CenterErrorException (ResponseMsgEnum.PAYTYPE_EXCEPTION);
//        }

        //------------------------step04 数据库t_order查询支付类型--------------------
        resp = dbService.queryOrderinfo(tradeParamMap);
        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYREFUND_EXCEPTION);   //订单退款查询失败
        } else {
            logger.info("refundOrder queryOrderinfo respdata = " + respdata);
            datalist = (ArrayList <Map <String, Object>>) datamap.get("dataList");
            Map<String, Object> data = datalist.get(0);//根据订单号查询，返回结果唯一
            tradeParamMap.put("payType", data.get("fPaytype"));// 支付类型匹配
            // datamap.put("ybThirdid", orderinfomap.get("f_ybthirdid"));// 医保自费使用的第三方支付ID
        }

        //------------------------step05 与支付宝、微信、银联通信，根据paytype判断使用何种支付方式进行订单退款结果查询--------------
        if ("01".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 支付宝
            logger.info("------------- queryRefund 支付宝-------------- ");
            resp = aliPayService.f2fRefundTradeQuery(tradeParamMap);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYREFUND_EXCEPTION);   //订单退款查询失败
            } else {
                //退款金额，可以不取
                Map<String, Object> data = (Map<String, Object>) respdata.get("data");
                tradeParamMap.put("refundAmount", data.get("refundAmount"));
                tradeParamMap.put("totalAmount", data.get("totalAmount"));
            }
        } else if ("02".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 微信
            logger.info("------------- queryRefund 微信 --------------- ");
            resp = wxPayService.refundQuery(tradeParamMap);
            logger.info("queryRefund wx resp = " + resp);
            respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
            if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000")) {
                throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYREFUND_EXCEPTION);   //订单退款查询失败
            } else {
                logger.info("queryRefund wx respdata = " + respdata);
                Map<String, Object> data = (Map<String, Object>) respdata.get("data");
                // String refund_status_$ = "refund_status_$" + (String) datamap.get("index");
                logger.info("queryRefund wx data = " + data);
                String refund_count = (String) data.get("refund_count");
                logger.info("queryRefund wx refund_count = " + refund_count);
                int count = new Integer(refund_count).intValue();
                count = count - 1;
                logger.info("queryRefund wx count = " + count);
                String refund_status = "refund_status_" + new Integer(count).toString();
                logger.info("queryRefund wx refund_status = " + refund_status);
                if ("SUCCESS".equals(data.get(refund_status))) {
                   // resultmap.put("returnCode", "0000");
                } else {
                    throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYREFUND_EXCEPTION);   //订单退款查询失败
                }
            }
        } else if ("04".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// epos--不做退费查询
            logger.info("------------- queryRefund epos --------------- ");
        } else if ("05".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 现金--不做退费查询
            logger.info("------------- queryRefund 现金 --------------- ");
        } else if ("06".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 医保--不做退费查询
            logger.info("------------- queryRefund 医保 --------------- ");
        } else if ("07".equals(tradeParamMap.get("payType").toString().substring(0, 2))) {// 工行聚合支付
            logger.info("------------- queryRefund 工行聚合支付 --------- ");// 商户需要开通权限才能做这个接口
        }

        tradeParamMap.put("order_status", "5");// t_order表状态，5表示已退款
        tradeParamMap.put("orderpay_status", "1");// t_order_pay表状态，1表示已确认

        //------------------------step06 更新t_orderd订单状态------------------------------
        resp = dbService.updateOrder(tradeParamMap);// 使用orderId为支付时产生的订单号

        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") ||!(Boolean)datamap.get ("updateflag")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYREFUND_EXCEPTION);   //订单退款查询失败
        }

        //------------------------step07 更新t_orderd_pay订单状态------------------------------
        resp = dbService.updateOrderrefund(tradeParamMap);// 使用refundorderId退费时产生的订单号

        respdata = JsonUtils.JsonToMapObj((String) resp.get("conResult"));
        datamap = (Map <String, Object>) respdata.get ("data");
        if (!resp.get("statuscode").equals("200") || !respdata.get("returnCode").equals("0000") || !(Boolean)datamap.get ("updateflag")) {
            throw new CenterErrorException (ResponseMsgEnum.TRADE_QUERYREFUND_EXCEPTION);   //订单退款查询失败
        }

        return ResponseUtils.success (resultmap);
    }

    /**
     * 解析国光支付宝网关返回的对账明细报文
     *
     * @param data
     * @param startindex
     * @param endindex
     * @return
     */
    private List <String> parseAliAccountData(String data, int startindex, int endindex) {
        logger.info("parseAliAccountData data = " + data);
        ArrayList<String> allString = new ArrayList<>();
        String[] arr = data.replace("~", "|").split("\\|", -1); // 用~分割
        for (int i = 0 + startindex; i < arr.length - 1 - endindex; i++) {
            allString.add(arr[i]);
        }
        return allString;
    }



    /**
     * 解析国光微信网关返回的对账明细报文 需要去掉非明细部分
     *
     * @param data
     * @param startindex
     * @param endindex
     * @return
     * @throws IOException
     */
    private List<String> parseWxAccountData(String data, int startindex, int endindex) throws IOException {
        logger.info("parseWxAccountData data = " + data);
        ArrayList<String> allString = new ArrayList<>();
        String[] arr = data.split("\\|", -1);   //使用|分割字符串
        for (int i = 0 + startindex; i < arr.length - 1 - endindex; i++) {
            allString.add(arr[i]);
        }
        return allString;
    }


}
