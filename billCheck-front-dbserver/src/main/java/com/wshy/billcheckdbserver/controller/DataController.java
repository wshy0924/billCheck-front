package com.wshy.billcheckdbserver.controller;


import com.alibaba.fastjson.JSONObject;
import com.wshy.billcheckdbserver.exception.CenterErrorException;
import com.wshy.billcheckdbserver.exception.ResponseInfo;
import com.wshy.billcheckdbserver.exception.ResponseMsgEnum;
import com.wshy.billcheckdbserver.serviceimpl.*;
import com.wshy.billcheckdbserver.utils.JsonUtils;
import com.wshy.billcheckdbserver.utils.MapUtils;
import com.wshy.billcheckdbserver.exception.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/28
 **/
@RestController
@RequestMapping(value = "/dbData",method = RequestMethod.POST)
public class DataController {
    private final static Logger logger = LoggerFactory.getLogger (DataController.class);

    @Autowired
    private ThirdTransService thirdTransService;
    @Autowired
    private HisTransService hisTransService;
    @Autowired
    private TOrderService tOrderService;
    @Autowired
    private TOrderPayService tOrderPayService;

    @RequestMapping("/")
    public ResponseInfo dbServerController(HttpServletRequest request , HttpServletResponse response,
                                              @RequestBody JSONObject jsonParam) throws CenterErrorException {
        logger.info("----------DataController controller----------");
        logger.info ("接收BillCheck-front请求数据 = {}" , jsonParam);

        Map <String, Object> reqMap = JsonUtils.JsonToMapObj(jsonParam.toJSONString());
        logger.info("paramMap = " + reqMap);
        //取出tradeType
        String tradeType = MapUtils.getMapParamValue(reqMap, "tradeCode", "");
        logger.info("tradeType:" + tradeType);

        Map<String,Object> tradeParamMap = (Map <String, Object>) reqMap.get ("tradeparam");
        logger.info ("tradeParamMap:" + tradeParamMap);

        //----------------step01 tradeType验证-------------
        if ("".equals (tradeType)) {
            throw new CenterErrorException (ResponseMsgEnum.UNEXPECTED_EXCEPTION,"数据插入失败，缺少tradeType");
        }else {
            //根据不同的tradeType处理不同的数据库业务
           return ResponseUtils.success (parseReturnData (tradeParamMap,tradeType));
        }
    }


    /**
     * 根据不同的tradeCode处理不同的数据库逻辑
     * @param tradeParamMap
     * @param tradeType
     * @return
     */
    private ResponseInfo parseReturnData(Map<String,Object> tradeParamMap,String tradeType) throws CenterErrorException {
        Map<String,Object> returnMap = new HashMap <> ();
        switch (tradeType) {
            case "t_third_trans.insertOrder":
                if (!thirdTransService.insertAll ("thirdtrans.insertThirdTransFromOut",tradeParamMap)) {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"create thirdBill failed！");
                }
                break;
            case "t_his_trans.insertOrder":
                if (!hisTransService.insertAll ("histrans.insertHisTransFromOut",tradeParamMap)) {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"create hisBill failed！");
                }
                break;
            case "t_order.insertOrder":
                if (!tOrderService.insertAll ("torder.insertOrder",tradeParamMap)) {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"create t_order failed！");
                }
                break;
            case "t_order_pay.insertOrder":
                if (!tOrderPayService.insertAll ("torderpay.insertOrderPay",tradeParamMap)) {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"create t_order_pay failed！");
                }
                break;
            case "t_order.selectByOrderTrace":
                List<Map> orderList = (List<Map>) tOrderService.selectService (tradeParamMap);   //使用orderservice中的selectService方法
                if (orderList.size() > 0) {
                    returnMap.put("orderInfo", orderList);
                } else {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"query order by ordertrace failed!");
                }
                break;
            case "t_order.updateOrderByOrderId":
                if (!tOrderService.update ("torder.updateOrderByOrderId",tradeParamMap)) {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"update order by OrderId failed!");
                }
                break;
            case "t_order_pay.updateOrderPayByOrderId":
                if (!tOrderPayService.update ("histrans.insertHisTransFromOut",tradeParamMap)) {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"update oreder pay by OrderId failed！");
                }
                break;
            case "t_order_pay.selectMaxIndexByordertrace":
                if (!hisTransService.insertAll ("histrans.insertHisTransFromOut",tradeParamMap)) {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"His对账数据插入失败！");
                }
                break;
            case "t_order_pay.insertOrderPay":
                if (!hisTransService.insertAll ("histrans.insertHisTransFromOut",tradeParamMap)) {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"His对账数据插入失败！");
                }
                break;
            case "t_order_pay.updateOrderRefundByOrderId":
                if (!hisTransService.insertAll ("histrans.insertHisTransFromOut",tradeParamMap)) {
                    throw new CenterErrorException (ResponseMsgEnum.DATABASE_EXCEPTION,"His对账数据插入失败！");
                }
                break;

            default:
                logger.info ("-------------------default-----------------------");
                throw new IllegalStateException ("Unexpected value: " + tradeType);
        }

        return null;

        }



}

