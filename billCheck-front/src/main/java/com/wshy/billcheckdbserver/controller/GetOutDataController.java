package com.wshy.billcheckdbserver.controller;

import com.wshy.billcheckdbserver.Exception.CenterErrorException;
import com.wshy.billcheckdbserver.Exception.ResponseInfo;
import com.wshy.billcheckdbserver.Exception.ResponseMsgEnum;
import com.wshy.billcheckdbserver.Utils.JsonUtils;
import com.wshy.billcheckdbserver.Utils.PayUtils;
import com.wshy.billcheckdbserver.Utils.ResponseUtils;
import com.wshy.billcheckdbserver.config.SystemConstants;
import com.wshy.billcheckdbserver.serviceImpl.GetOutBillAccServiceImpl;
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
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/19
 **/
@RestController
@RequestMapping(value = "/lay",method = RequestMethod.POST)
public class GetOutDataController {

    private static final Logger logger = LoggerFactory.getLogger (GetOutBillAccServiceImpl.class);

    @Autowired
    private GetOutBillAccServiceImpl getOutBillAccService;

    @RequestMapping("/getOutThirdData")
    public ResponseInfo getOutThirdBillData(HttpServletRequest req, HttpServletResponse resp, @RequestBody String jsonreq) throws Exception {
        Map<String,Object> requestmap = new HashMap <> ();     //接收请求的map
        Map<String,Object> tradeParamMap = new HashMap <> ();  //请求中tradeParam部分
        Map<String,Object> datamap  = new HashMap <> ();       //返回替中响应datamap
        logger.info ("getOutThirdData接收请求数据 = {}", jsonreq);


        requestmap = JsonUtils.JsonToMapObj (jsonreq);
        tradeParamMap = (Map <String, Object>) requestmap.get ("tradeParamMap");
        String sign = (String) requestmap.get ("sign");

        //----------------step01 tradeType校验-------------
        if (!vertifytradType(datamap,"Out_ThirdData")) {
           throw new CenterErrorException (ResponseMsgEnum.TRADETYPE_EXCEPTION);    //tradeType错误
        }

        //----------------step02 sign校验-------------------
        if (!vertifysign (tradeParamMap,sign)) {
            throw new CenterErrorException (ResponseMsgEnum.SIGN_EXCEPTION);        //sign错误
        }

        //----------------step03 tradeParam参数校验----------



        return ResponseUtils.success (resp);
    }

    /**
     * 验证tradeType
     * @param reqmap
     * @param tradeType
     * @return
     */
    public Boolean vertifytradType (Map<String,Object> reqmap,String tradeType) {
        logger.info ("--------------step01 - 验证请求tradetype-------------");
        if (tradeType.equals (reqmap.get ("tradeType"))) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 验证sign
     * @param reqmap
     * @param sign
     * @return
     * @throws Exception
     */
    public Boolean vertifysign(Map<String,Object>reqmap, String sign) throws Exception {

        String data = JsonUtils.MapToJson (reqmap);
        logger.info ("verifySign data = " + data);
        StringBuilder sb = new StringBuilder ();
        sb.append (data);
        sb.append ("&key:");
        sb.append (SystemConstants.Platform_Key);

        if (sign.equals (PayUtils.MD5 (sb.toString ()))) {
            return true;
        } else {
            return false;
        }
    }


}
