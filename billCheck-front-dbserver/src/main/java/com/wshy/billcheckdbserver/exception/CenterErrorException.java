package com.wshy.billcheckdbserver.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author wshy
 * @data 2020/6/17
 **/
@Data
@ResponseStatus
public class CenterErrorException extends Exception{
    private static final long serialVersionUID = -7480022450501760611L;
    /**
     * http状态码
     */

    private HttpStatus status;

    /**
     * 异常码
     */
    private Object code;
    /**
     * 异常提示信息
     */
    private String msg;

    public  CenterErrorException(String returnCode, String msg){
        this.code = returnCode;
        this.msg = msg;
    }

    /**
     * 自定义异常，使用枚举类中的异常码和异常信息
     * @param responseMsgEnum
     */
    public CenterErrorException(ResponseMsgEnum responseMsgEnum) {
        this.code = responseMsgEnum.getCode();
        this.msg = responseMsgEnum.getMsg();
    }

    /**
     * 用户自定义异常消息内容和异常消息内容
     * @param responseMsgEnum
     * @param msg，用户自定义消息内容
     */
    public CenterErrorException(ResponseMsgEnum responseMsgEnum, String msg) {
        this.code = responseMsgEnum.getCode();
        this.msg = msg;
    }

    /**
     * 传入系统异常代码和自定义异常内容msg
     * @param httpStatus
     */
    public CenterErrorException(HttpStatus httpStatus, String msg) {
//        this.status = httpStatus;
        this.code = httpStatus.value();
        this.msg = msg;
    }

    /**
     * 传入系统异常代码
     * @param httpStatus
     */
    public CenterErrorException(HttpStatus httpStatus) {
        //this.status = httpStatus;
        this.code = httpStatus.value();
        this.msg = httpStatus.getReasonPhrase();
    }

    public CenterErrorException (Object code ) {

        this.code = code;
    }

}
