package com.wshy.billcheck.config;

import java.util.HashMap;

/**
 * @author wshy
 * @data 2020/6/24
 **/
public class GGPayConstants {
    public static final String URL = "http:/139.196.227.77:27002/dbData/";


    public static HashMap <String, String> productmap = new HashMap<String, String>();

    public static HashMap<String, String> thirdtradetypemap = new HashMap<String, String>();

    static {
        productmap.put("8410001", "门诊挂号");
        productmap.put("8410002", "门诊缴费");
        productmap.put("8410003", "门诊充值");
        productmap.put("8410004", "住院押金");
    }

    static {
        thirdtradetypemap.put("0101", "F2FBarCodePay");
        thirdtradetypemap.put("0102", "F2FScanQrCodePay");
        thirdtradetypemap.put("0103", "F2FWindowPay");
        thirdtradetypemap.put("0201", "MicroPay");
        thirdtradetypemap.put("0202", "UnifiedPayNATIVE");
        thirdtradetypemap.put("0203", "WechatWindowPay");
        thirdtradetypemap.put("0301", "UnionBarCodePay");
        thirdtradetypemap.put("0302", "UnionScanQrCodePay");
        thirdtradetypemap.put("0401", "EposPay");
        thirdtradetypemap.put("0501", "CashPay");
        thirdtradetypemap.put("0601", "YbPay");
        thirdtradetypemap.put("0701", "IcbcpayQrcode");
        thirdtradetypemap.put("0702", "IcbcgenerateQrcode");
        thirdtradetypemap.put("0703", "IcbcF2FWindowPay");
        thirdtradetypemap.put("0704", "IcbcF2FWechatWindowPay");
    }
}
