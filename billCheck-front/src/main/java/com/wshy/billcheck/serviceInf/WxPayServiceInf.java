package com.wshy.billcheck.serviceInf;

import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public interface WxPayServiceInf {


        Map <String, Object> unifiedPayforNATIVE(Map<String, Object> map) throws Exception;     //微信native支付，商户系统生成二维码，用户扫码支付

        Map<String, Object> unifiedPay(Map<String, Object> map) throws Exception;               //微信公众号支付，统一支付下单

        Map<String, Object> orderQuery(Map<String, Object> map) throws Exception;               //订单查询

        Map<String, Object> orderRefund(Map<String, Object> map) throws Exception;              //申请退款

        Map<String, Object> refundQuery(Map<String, Object> map) throws Exception;              //退款查询

        Map<String, Object> microPay(Map<String, Object> map) throws Exception;                 //刷卡支付

        Map<String, Object> closeOrder(Map<String, Object> map) throws Exception;               //关闭订单

        Map<String, Object> downloadBill(Map<String, Object> map) throws Exception;             //下载对账单

        Map<String, Object> getSign(Map<String, Object> map) throws Exception;                  //获取签名



}
