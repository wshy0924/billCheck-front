package com.wshy.billcheckdbserver.exception;


import com.wshy.billcheckdbserver.exception.ResponseInfo;
import com.wshy.billcheckdbserver.exception.ResponseMsgEnum;

public class ResponseUtils {
    /**
     * 成功时返回
     * @param object
     * @return
     */
    public static ResponseInfo success(Object object) {

        return new ResponseInfo(ResponseMsgEnum.NORMAL_RETURN.getCode(),ResponseMsgEnum.NORMAL_RETURN.getMsg(),object);
    }

    /**
     * 失败时返回
     * @param
     * @return
     */

    public  static  ResponseInfo failure(ResponseMsgEnum responseMsgEnum,Object obj) {

        return  new ResponseInfo(responseMsgEnum.getCode(),responseMsgEnum.getMsg(),obj);
    }

    /**
     * 异常时返回
     * @param
     * @return
     */
    public  static  ResponseInfo Exception(Object code, String msg) {

        return  new ResponseInfo(code,msg);
    }

    /**
     * 异常时返回
     * @param
     * @return
     */
    public static ResponseInfo ExceptionGlobal(Exception e) {

        return  new ResponseInfo(e);

    }

}
