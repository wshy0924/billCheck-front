package com.wshy.billcheck.serviceImpl;

import com.wshy.billcheck.HttpRequest.WxPayRequest;
import com.wshy.billcheck.Utils.JsonUtils;
import com.wshy.billcheck.Utils.PayUtils;
import com.wshy.billcheck.config.WXPayConstants;
import com.wshy.billcheck.serviceInf.WxPayServiceInf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public class WxPayServiceImpl implements WxPayServiceInf {
    private final static Logger logger = LoggerFactory.getLogger (WxPayServiceImpl.class);

    @Autowired
    private WxPayRequest wxPayRequest;

    /**
     *微信native支付，商会后台根据订单金额等信息，请求微信后台，并生成支付的二维码，
     * 用户扫描二维码后支付
    * @param map
     * @return
     */
    @Override
    public Map <String, Object> unifiedPayforNATIVE (Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap <String, Object> ();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> wxpaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        wxpaymap = (Map<String, Object>) WXPayConstants.wxpayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("body", map.get("fProduct"));             //商品名称，例如当日挂号
        tradeparammap.put("detail", "");                            //可以为空
        tradeparammap.put("attach", "unifiedPay");
        tradeparammap.put("total_fee", (String) map.get("settleAmt"));
        tradeparammap.put("spbill_create_ip", "127.0.0.1");         //IP,可以随便填
        tradeparammap.put("openid", (String) map.get("onlyId"));    //微信支付用户的openid
        tradeparammap.put("out_trade_no", (String) map.get("fOrdertrace"));
        tradeparammap.put("trade_type", "JSAPI");
        tradeparammap.put("product_id", (String) map.get("orderType"));//产品编号
        //tradeparammap.put("limit_pay", "");
        tradeparammap.put("pid", (String)wxpaymap.get("pId"));
        tradeparammap.put("appid", (String)wxpaymap.get("appId"));

        req.put("tradeType", "UnifiedPay");
        req.put("tradeParam", tradeparammap);
        req.put("tradeRemark", "");
        req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, (String)wxpaymap.get("md5Key")));
        resp = wxPayRequest.dopost((String)wxpaymap.get("URL"), JsonUtils.MapToJson(req));

        return resp;
    }

    /**
     * 微信公众号支付，统一下单
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> unifiedPay (Map <String, Object> map) throws Exception {

        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> wxpaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        wxpaymap = (Map<String, Object>)WXPayConstants.wxpayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("body", map.get("fProduct"));             //商品名称，例如当日挂号
        tradeparammap.put("detail", "");                            //可以为空
        tradeparammap.put("attach", "unifiedPay");
        tradeparammap.put("total_fee", (String) map.get("settleAmt"));
        tradeparammap.put("spbill_create_ip", "127.0.0.1");         //IP,可以随便填
        tradeparammap.put("openid", (String) map.get("onlyId"));    //微信支付用户的openid
        tradeparammap.put("out_trade_no", (String) map.get("fOrdertrace"));
        tradeparammap.put("trade_type", "JSAPI");
        tradeparammap.put("product_id", (String) map.get("orderType"));//产品编号
        //tradeparammap.put("limit_pay", "");
        tradeparammap.put("pid", (String)wxpaymap.get("pId"));
        tradeparammap.put("appid", (String)wxpaymap.get("appId"));

        req.put("tradeType", "UnifiedPay");
        req.put("tradeParam", tradeparammap);
        req.put("tradeRemark", "");
        req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, (String)wxpaymap.get("md5Key")));
        resp = wxPayRequest.dopost((String)wxpaymap.get("URL"), JsonUtils.MapToJson(req));

        return resp;
    }

    /**
     * 订单查询
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> orderQuery (Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> wxpaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        wxpaymap = (Map<String, Object>)WXPayConstants.wxpayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("out_trade_no", (String) map.get("orderId"));
        tradeparammap.put("pid", (String)wxpaymap.get("pId"));
        tradeparammap.put("appid", (String)wxpaymap.get("appId"));
        req.put("tradeType", "OrderQuery");
        req.put("tradeParam", tradeparammap);
        req.put("tradeRemark", "");
        req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, (String)wxpaymap.get("md5Key")));
        resp = wxPayRequest.dopost((String)wxpaymap.get("URL"), JsonUtils.MapToJson(req));

        return resp;

    }

    /**
     * 订单退款
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> orderRefund (Map <String, Object> map) throws Exception {

        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> wxpaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        wxpaymap = (Map<String, Object>)WXPayConstants.wxpayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("out_trade_no", (String) map.get("orderId"));
        tradeparammap.put("out_refund_no", (String) map.get("fOrdertrace"));
        //tradeparammap.put("total_fee", (String) data.get("settleAmt"));
        tradeparammap.put("total_fee", (String) map.get("refundAmt"));  //平台不支持部分退费
        tradeparammap.put("refund_fee", (String) map.get("refundAmt")); //退款金额传订单总金额
        tradeparammap.put("pid", (String)wxpaymap.get("pId"));
        tradeparammap.put("appid", (String)wxpaymap.get("appId"));
        req.put("tradeType", "OrderRefund");
        req.put("tradeParam", tradeparammap);
        req.put("tradeRemark", "");
        req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, (String)wxpaymap.get("md5Key")));
        resp = wxPayRequest.dopost((String)wxpaymap.get("URL"), JsonUtils.MapToJson(req));

        return resp;
    }

    /**
     * 退款查询
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> refundQuery (Map <String, Object> map) throws Exception {

        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> wxpaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        wxpaymap = (Map<String, Object>)WXPayConstants.wxpayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("out_trade_no", (String) map.get("orderId"));
        tradeparammap.put("pid", (String)wxpaymap.get("pId"));
        tradeparammap.put("appid", (String)wxpaymap.get("appId"));
        req.put("tradeType", "RefundQuery");
        req.put("tradeParam", tradeparammap);
        req.put("tradeRemark", "");
        req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, (String)wxpaymap.get("md5Key")));
        resp = wxPayRequest.dopost((String)wxpaymap.get("URL"), JsonUtils.MapToJson(req));

        return resp;
    }

    /**
     * 刷卡支付
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> microPay (Map <String, Object> map) throws Exception {

        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> wxpaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        wxpaymap = (Map<String, Object>)WXPayConstants.wxpayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("body", map.get("fProduct"));         //商品名称，例如当日挂号
        tradeparammap.put("detail", "");                        //可以为空
        tradeparammap.put("attach", "");                        //可以为空
        tradeparammap.put("total_fee", (String) map.get("settleAmt"));
        tradeparammap.put("spbill_create_ip", "127.0.0.1");     //IP,可以随便填
        tradeparammap.put("auth_code", (String) map.get("thirdAuthCode"));
        tradeparammap.put("out_trade_no", (String) map.get("fOrdertrace"));
        tradeparammap.put("pid", (String)wxpaymap.get("pId"));
        tradeparammap.put("appid", (String)wxpaymap.get("appId"));
        req.put("tradeType", "MicroPay");
        req.put("tradeParam", tradeparammap);
        req.put("tradeRemark", "");
        req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, (String)wxpaymap.get("md5Key")));
        resp = wxPayRequest.dopost((String)wxpaymap.get("URL"), JsonUtils.MapToJson(req));

        return resp;
    }

    /**
     * 关闭订单
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> closeOrder (Map <String, Object> map) throws Exception {

        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> wxpaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        wxpaymap = (Map<String, Object>)WXPayConstants.wxpayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("out_trade_no", (String) map.get("orderId"));
        tradeparammap.put("pid", (String)wxpaymap.get("pId"));
        tradeparammap.put("appid", (String)wxpaymap.get("appId"));
        req.put("tradeType", "CloseOrder");
        req.put("tradeParam", tradeparammap);
        req.put("tradeRemark", "");
        req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, (String)wxpaymap.get("md5Key")));
        resp = wxPayRequest.dopost((String)wxpaymap.get("URL"), JsonUtils.MapToJson(req));

        return resp;
    }

    /**
     * 下载对账单
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> downloadBill (Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> wxpaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        wxpaymap = (Map<String, Object>)WXPayConstants.wxpayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("bill_date", (String) map.get("bill_date"));
        tradeparammap.put("bill_type", "ALL");
        tradeparammap.put("pid", (String)wxpaymap.get("pId"));
        tradeparammap.put("appid", (String)wxpaymap.get("appId"));
        req.put("tradeType", "DownloadBill");
        req.put("tradeParam", tradeparammap);
        req.put("tradeRemark", "");
        req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, (String)wxpaymap.get("md5Key")));
        resp = wxPayRequest.dopost((String)wxpaymap.get("URL"), JsonUtils.MapToJson(req));

        return resp;
    }

    /**
     * 获取签名
     * @param map
     * @return
     */
    @Override
    public Map <String, Object> getSign (Map <String, Object> map) throws Exception {
        Map<String, Object> req = new HashMap<String, Object>();
        Map<String, Object> tradeparammap = new HashMap<String, Object>();
        Map<String, Object> wxpaymap = new HashMap<String, Object>();
        Map<String, Object> resp = new HashMap<String, Object>();

        wxpaymap = (Map<String, Object>)WXPayConstants.wxpayConstantsmap.get(map.get("merchantId"));
        tradeparammap.put("timeStamp", map.get("timeStamp"));
        tradeparammap.put("nonceStr", map.get("nonceStr"));
        tradeparammap.put("package", "prepay_id="+map.get("thirdCode"));
        tradeparammap.put("signType", "MD5");

        tradeparammap.put("pid", (String)wxpaymap.get("pId"));
        tradeparammap.put("appid", (String)wxpaymap.get("appId"));

        req.put("tradeType", "MD5Sign");
        req.put("tradeParam", tradeparammap);
        req.put("tradeRemark", "");
        req.put("sign", PayUtils.wxgenerateSignature(tradeparammap, (String)wxpaymap.get("md5Key")));
        resp = wxPayRequest.dopost((String)wxpaymap.get("URL"), JsonUtils.MapToJson(req));

        return resp;
    }
}
