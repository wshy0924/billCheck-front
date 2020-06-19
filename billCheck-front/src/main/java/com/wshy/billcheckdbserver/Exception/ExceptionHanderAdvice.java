package com.wshy.billcheckdbserver.Exception;

import com.wshy.billcheckdbserver.Utils.ParamUtils;
import com.wshy.billcheckdbserver.Utils.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author wshy
 * @data 2020/6/17
 **/
@RestControllerAdvice
public class ExceptionHanderAdvice {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 自定义拦截业务异常，返回业务异常信息
     * @param
     * @return
     */
    @ExceptionHandler(CenterErrorException.class)
    //@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseInfo handleBusinessError(CenterErrorException centerErrorException) {

        Object code = centerErrorException.getCode();
        String msg = centerErrorException.getMsg();
        //HttpStatus status = centerErrorException.getStatus();

        return ResponseUtils.Exception(code,msg);
    }

    /**
     * 全局异常捕获
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseInfo globalRequestException(Exception exception) {
        return new ResponseInfo(ParamUtils.getErrorCode(exception), ParamUtils.getError(exception));
    }

}
