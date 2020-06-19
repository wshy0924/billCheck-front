package com.wshy.billcheckdbserver.Exception;

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
     * 参数过大
     */
    PARMETER_BIG_EXCEPTION("903", "内容不能超过20个字，请重试!"),
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
