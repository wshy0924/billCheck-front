package com.wshy.billcheck.Exception;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public enum ResponseMsgEnum {
    /**
     * 正常返回
     */
    NORMAL_RETURN("0000","请求成功！"),
    /**
     * 请求失败
     */
    INNORMAL_RETURN("9999","请求失败！"),
    /**
     * 交易码错误
     */
    TRADETYPE_EXCEPTION("101", "交易码错误!"),
    /**
     * 签名错误
     */

    SIGN_EXCEPTION("102", "签名错误！"),

    /**
     * 参数为空
     */
    PARMETER_EXCEPTION("103", "请求参数存在空值!"),
    /**
     * 参数key为空
     */
    PARMETERKEY_EXCEPTION("104", "请求参数KEY不能为空！"),

    REQUEST_RESP_EXCEPTION("601","http请求返回异常！"),


    /**
     * 数据库操作失败
     */
    DATABASE_EXCEPTION("400", "数据库操作异常，请联系管理员！"),
    /**
     * 500 : 一劳永逸的提示也可以在这定义
     */
    UNEXPECTED_EXCEPTION("501", "系统发生异常，请联系管理员！");
    // 还可以定义更多的业务异常

    /**
     * 消息码
     */
    private String code;
    /**
     * 消息内容
     */
    private String msg;

    private ResponseMsgEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
