package com.wshy.billcheck.serviceInf;

import com.wshy.billcheck.Exception.CenterErrorException;

import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/24
 **/
public interface GGpayServiceInf {

    Map<String,Object> insertOrder(Map<String,Object> map) throws CenterErrorException;        //使用国光统一支付平台时，插入订单信息
    Map<String,Object> insertThirdTrans(Map<String,Object> map) throws CenterErrorException;   //插入第三方对账数据
    Map<String,Object> insertHisTrans(Map<String,Object> map) throws CenterErrorException;     //插入his对账数据

}
