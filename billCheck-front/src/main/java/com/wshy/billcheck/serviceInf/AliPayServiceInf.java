package com.wshy.billcheck.serviceInf;

import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public interface AliPayServiceInf {


        Map <String, Object> f2fScanQrCodePay(Map<String, Object> map) throws Exception;            //扫码支付

        Map<String, Object> f2fQueryPayResultForLoop(Map<String, Object> map) throws Exception;     //轮询支付结果

        Map<String, Object> f2fRefundTradePay(Map<String, Object> map) throws Exception;            //支付宝退款

        Map<String, Object> f2fRefundTradeQuery(Map<String, Object> map) throws Exception;          //退款查询

        Map<String, Object> f2fBarCodePay(Map<String, Object> map) throws Exception;                //二维码被扫支付

        Map<String, Object> f2fCancelPay(Map<String, Object> map) throws Exception;                 //撤销订单

        Map<String, Object> f2fDuiZhang(Map<String, Object> map) throws Exception;                  //对账

        Map<String, Object> tradeCreate(Map<String, Object> map) throws Exception;                  //统一收单创建

    }


