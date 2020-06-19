package com.wshy.billcheckdbserver.serviceImpl;

import com.wshy.billcheckdbserver.HttpRequest.AliPayRequest;
import com.wshy.billcheckdbserver.Utils.JsonUtils;
import com.wshy.billcheckdbserver.Utils.PayUtils;
import com.wshy.billcheckdbserver.config.ALIPayConstants;
import com.wshy.billcheckdbserver.serviceInf.AliPayServiceInf;
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
public class AliPayServiceImpl implements AliPayServiceInf {
    private final static Logger logger = LoggerFactory.getLogger(AliPayServiceImpl.class);

    @Autowired
    private AliPayRequest aliPayRequest;

    /**
     * 扫码支付，获取二维码
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> f2fScanQrCodePay(Map <String, Object> map) throws Exception {
        // TODO Auto-generated method stub
        Map<String, Object> reqmap = new HashMap <String, Object>();           //请求支付宝网关map
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> alipaymap = new HashMap<String, Object>();         //ALIPayConstants中参数map
        Map<String, Object> resp = new HashMap<String, Object>();

        alipaymap = (Map<String, Object>) ALIPayConstants.alipayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("body", " ");                                     //要加个空格
        tradeparammap.put("subject", map.get("fProduct"));	                //订单标题，与数据库传入的标题一致
        tradeparammap.put("totalAmount", (String) map.get("settleAmt"));
        tradeparammap.put("outTradeNo", (String) map.get("fOrdertrace"));   //商户唯一交易订单号，平台生成
        tradeparammap.put("storeId", " ");                                  //门店号，设备号
        tradeparammap.put("pid", (String)alipaymap.get("pId"));             //商户号
        tradeparammap.put("appid", (String)alipaymap.get("appId"));         //应用id号
        reqmap.put("tradeType", "F2FScanQrCodePay");
        reqmap.put("tradeParam", tradeparammap);

        reqmap.put("sign", PayUtils.aligenerateSignature(tradeparammap, (String)alipaymap.get("md5Key")));

        resp = aliPayRequest.dopost((String)alipaymap.get("URL"), JsonUtils.MapToJson(reqmap));
        logger.info("f2fScanQrCodePay resp = " + resp);

        return resp;
    }

    /**
     * C端轮询支付结果
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> f2fQueryPayResultForLoop(Map <String, Object> map) throws Exception {
        // TODO Auto-generated method stub
        Map<String, Object> reqmap = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> alipaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        alipaymap = (Map<String, Object>)ALIPayConstants.alipayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("outTradeNo", (String) map.get("orderId"));

        tradeparammap.put("pid", (String)alipaymap.get("pId"));
        tradeparammap.put("appid", (String)alipaymap.get("appId"));
        reqmap.put("tradeType", "F2FQueryPayResultForLoop");
        reqmap.put("tradeParam", tradeparammap);

        reqmap.put("sign", PayUtils.aligenerateSignature(tradeparammap, (String)alipaymap.get("md5Key")));
        resp = aliPayRequest.dopost((String)alipaymap.get("URL"), JsonUtils.MapToJson(reqmap));
        logger.info("f2fQueryPayResultForLoop resp = " + resp);

        return resp;

    }

    /**
     * 支付宝退款
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> f2fRefundTradePay(Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> alipaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        alipaymap = (Map<String, Object>)ALIPayConstants.alipayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("outTradeNo", (String) map.get("orderId"));
        tradeparammap.put("pid", (String)alipaymap.get("pId"));
        tradeparammap.put("appid", (String)alipaymap.get("appId"));
        tradeparammap.put("subject", map.get("refundReason"));
        tradeparammap.put("totalAmount", (String) map.get("refundAmt"));
        tradeparammap.put("storeId", " ");                                  //门店号，设备号

        req.put("tradeType", "F2FRefundTradePay");
        req.put("tradeParam", tradeparammap);

        req.put("sign", PayUtils.aligenerateSignature(tradeparammap, (String)alipaymap.get("md5Key")));
        resp = aliPayRequest.dopost((String)alipaymap.get("URL"), JsonUtils.MapToJson(req));
        logger.info("f2fQueryPayResultForLoop resp = " + resp);

        return resp;

    }

    /**
     * 退款查询
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> f2fRefundTradeQuery(Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> alipaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        alipaymap = (Map<String, Object>)ALIPayConstants.alipayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("outTradeNo", (String) map.get("orderId"));
        tradeparammap.put("pid", (String)alipaymap.get("pId"));
        tradeparammap.put("appid", (String)alipaymap.get("appId"));
        req.put("tradeType", "F2FRefundTradeQuery");
        req.put("tradeParam", tradeparammap);
        req.put("sign", PayUtils.aligenerateSignature(tradeparammap, (String)alipaymap.get("md5Key")));

        resp = aliPayRequest.dopost((String)alipaymap.get("URL"), JsonUtils.MapToJson(req));
        logger.info("f2fRefundTradeQuery resp = " + resp);

        return resp;

    }

    /**
     * 二维码/条形码被扫
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> f2fBarCodePay(Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> alipaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();


        alipaymap = (Map<String, Object>)ALIPayConstants.alipayConstantsmap.get(map.get("merchantId"));

        tradeparammap.put("pid", (String)alipaymap.get("pId"));
        tradeparammap.put("appid", (String)alipaymap.get("appId"));
        tradeparammap.put("scene", "bar_code");	                          //支付场景，如果没有默认走扫码支付 场景，如果填写采用对应的支付场景支付
        tradeparammap.put("outTradeNo", (String) map.get("fOrdertrace"));//商户唯一交易订单号
        tradeparammap.put("subject", map.get("fProduct"));               //订单标题
        tradeparammap.put("totalAmount", (String) map.get("settleAmt"));
        tradeparammap.put("authCode", (String) map.get("thirdAuthCode"));//当面付：录入扫码枪扫到的条码信息。
        tradeparammap.put("storeId", " ");                                //商户门店编号
        //tradeparammap.put("terminalId", "");                            //商户机具终端编号,扫码支付--选填,刷脸支付--必填
        tradeparammap.put("body", " ");                                   //订单描述
        req.put("tradeType", "F2FBarCodePay");
        req.put("tradeParam", tradeparammap);

        req.put("sign", PayUtils.aligenerateSignature(tradeparammap, (String)alipaymap.get("md5Key")));
        resp = aliPayRequest.dopost((String)alipaymap.get("URL"), JsonUtils.MapToJson(req));
        logger.info("f2fBarCodePay resp = " + resp);

        return resp;

    }

    /**
     * 取消交易
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> f2fCancelPay(Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> alipaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        alipaymap = (Map<String, Object>)ALIPayConstants.alipayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("outTradeNo", (String) map.get("orderId"));
        tradeparammap.put("pid", (String)alipaymap.get("pId"));
        tradeparammap.put("appid", (String)alipaymap.get("appId"));
        req.put("tradeType", "F2FCancelPay");
        req.put("tradeParam", tradeparammap);
        req.put("sign", PayUtils.aligenerateSignature(tradeparammap, (String)alipaymap.get("md5Key")));

        resp = aliPayRequest.dopost((String)alipaymap.get("URL"), JsonUtils.MapToJson(req));
        logger.info("f2fCancelPay resp = " + resp);

        return resp;
    }

    /**
     * 对账
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> f2fDuiZhang(Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> alipaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        alipaymap = (Map<String, Object>)ALIPayConstants.alipayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("bill_type", "trade");
        tradeparammap.put("bill_date", (String) map.get("bill_date"));
        tradeparammap.put("pid", (String)alipaymap.get("pId"));         //商户号
        tradeparammap.put("appid", (String)alipaymap.get("appId"));     //比如商户下开通的当面付应用的id
        req.put("tradeType", "F2FDuiZhang");
        req.put("tradeParam", tradeparammap);

        req.put("sign", PayUtils.aligenerateSignature(tradeparammap, (String)alipaymap.get("md5Key")));

        resp = aliPayRequest.dopost((String)alipaymap.get("URL"), JsonUtils.MapToJson(req));
        logger.info("f2fDuiZhang resp = " + resp);

        return resp;
    }

    /**
     * 统一收单交易创建
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> tradeCreate(Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> alipaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        alipaymap = (Map<String, Object>)ALIPayConstants.alipayConstantsmap.get(map.get("merchantId"));

        tradeparammap.put("body", " ");                                     //要加个空格
        tradeparammap.put("subject", map.get("fProduct"));
        tradeparammap.put("totalAmount", (String) map.get("settleAmt"));
        tradeparammap.put("outTradeNo", (String) map.get("fOrdertrace"));
        tradeparammap.put("storeId", " ");                                  //门店号，设备号
        tradeparammap.put("pid", (String)alipaymap.get("pId"));
        tradeparammap.put("appid", (String)alipaymap.get("appId"));
        tradeparammap.put("buyerId", (String) map.get("onlyId"));

        req.put("tradeType", "TradeCreate");
        req.put("tradeParam", tradeparammap);

        req.put("sign", PayUtils.aligenerateSignature(tradeparammap, (String)alipaymap.get("md5Key")));
        resp = aliPayRequest.dopost((String)alipaymap.get("URL"), JsonUtils.MapToJson(req));
        logger.info("tradeCreate resp = " + resp);

        return resp;
    }
}
