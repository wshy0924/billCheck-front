package com.wshy.billcheckdbserver.serviceInf;

import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public interface GetOutBillAccServiceInf {

    Map <String,Object> getOutThirdBill (Map<String,Object> map) throws Exception;   //获取第三方对账数据（外部）

    Map<String,Object> getOutHisBill (Map<String,Object> map) throws Exception;      //获取his对账数据（外部）
}
