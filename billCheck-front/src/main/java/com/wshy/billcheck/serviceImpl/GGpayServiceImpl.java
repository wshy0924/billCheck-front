package com.wshy.billcheck.serviceImpl;

import com.wshy.billcheck.Exception.CenterErrorException;
import com.wshy.billcheck.HttpRequest.DBServerRequest;
import com.wshy.billcheck.Utils.JsonUtils;
import com.wshy.billcheck.config.GGPayConstants;
import com.wshy.billcheck.serviceInf.GGpayServiceInf;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * @author wshy
 * @data 2020/6/24
 **/
@Service
public class GGpayServiceImpl implements GGpayServiceInf {
    private static final Logger logger = Logger.getLogger (GGpayServiceImpl.class);

    @Autowired
    private DBServerRequest dbServerRequest;


    @Override
    public Map <String, Object> insertOrder (Map <String, Object> data) throws CenterErrorException {
        Map<String, Object> req = new HashMap <String, Object> ();
        Map<String, Object> resp = new HashMap<String, Object>();
        req.put("tradeCode", "t_third_trans.insertOrder");      //数据库服务根据此字段确定操作哪个数据库
        req.put("fMerchantid", data.get("merchantId"));         //商户号
        req.put("fOrdertrace", data.get("fOrdertrace"));        //订单号
        req.put("fChannel", data.get("chanelType"));            //01自助终端，02人工窗口
        req.put("fOperid", data.get("operId"));                 //操作员号
        req.put("fTermid", data.get("termId"));                 //终端号
        req.put("fTermtrace", "0");                             //终端流水号
        req.put("fDepart", data.get("depart"));                 //科室
        req.put("fRegdate", data.get("fRegdate"));              //日期
        req.put("fRegtime", data.get("fRegtime"));              //时间
        req.put("fOrdertype", data.get("orderType"));           //8410002门诊缴费,8410003门诊充值,8410004住院押金
        req.put("fThirdid", data.get("payType").toString().substring(0, 2));        //01支付宝、02微信、03银联、04EPOS、05现金、06医保
        req.put("fProduct", GGPayConstants.productmap.get(data.get("orderType")));  //商品描述--还需处理--需要注意更新Map
        req.put("fPaytype", data.get("payType"));               //0102支付宝商户被扫,0202微信商户被扫,手机扫描二维码
        req.put("fTradetype", GGPayConstants.thirdtradetypemap.get(data.get("payType")));//需要注意更新Map
        req.put("fYbflag", data.get("ybFlag"));                 //0不参与医保，1参与医保
        //req.put("fYbthirdid", data.get("ybthirdId"));
        req.put("fOrderamt", data.get("orderAmt"));             //订单总额(分)
        req.put("fSettleamt", data.get("settleAmt"));           //付款金额(分)
        req.put("fPlanamt", data.get("planAmt"));               //医保统筹金额(分)
        req.put("fStatus", "1");                                //订单状态，0待创建，1未支付，2正在支付，3已支付，4已关闭，5已退款
        req.put("fBuyername", data.get("buyerName"));           //付款人姓名
        req.put("fBuyertel", data.get("buyerTel"));             //付款人电话
        req.put("fRemark", "");                                 //预留字段
        resp = dbServerRequest.dopost(GGPayConstants.URL, JsonUtils.MapToJson(req));//插入数据库
        resp.putAll(req);//把传入信息
        logger.info("GGPayServiceImpl createOrder resp = " + resp);
        return resp;
    }

    /**
     * 插入第三方记账流水表
     * @param map
     * @return
     * @throws CenterErrorException
     */
    @Override
    public Map <String, Object> insertThirdTrans (Map <String, Object> map) throws CenterErrorException {
        Map<String,Object> req = new HashMap <> ();     //请求数据库服务的map
        Map<String,Object> resp = new HashMap <> ();    //数据库服务返回map

        req.put("tradeCode", "t_third_trans.insertOrder");      //数据库服务根据此字段确定操作哪个数据库
        req.put ("tradeParam",map);

        resp = dbServerRequest.dopost (GGPayConstants.URL,JsonUtils.MapToJson (req));
        logger.info ("GGPayServiceImpl insertThirdTrans resp = " + resp);
        return resp;
    }

    /**
     * 插入his记账流水表
     * @param map
     * @return
     * @throws CenterErrorException
     */
    @Override
    public Map <String, Object> insertHisTrans (Map <String, Object> map) throws CenterErrorException {
        Map<String,Object> req = new HashMap <> ();     //请求数据库服务的map
        Map<String,Object> resp = new HashMap <> ();    //数据库服务返回map

        req.put("tradeCode", "t_his_trans.insertOrder");      //数据库服务根据此字段确定操作哪个数据库
        req.put ("tradeParam",map);

        resp = dbServerRequest.dopost (GGPayConstants.URL,JsonUtils.MapToJson (req));
        logger.info ("GGPayServiceImpl insertHisTrans resp = " + resp);
        return resp;
    }
}
