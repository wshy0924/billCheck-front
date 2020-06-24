package com.wshy.billcheck.controller;

import com.alibaba.fastjson.JSONObject;
import com.wshy.billcheck.Entity.vertifyEntity;
import com.wshy.billcheck.Exception.CenterErrorException;
import com.wshy.billcheck.Exception.ResponseInfo;
import com.wshy.billcheck.Utils.ResponseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/lay", method = RequestMethod.GET)
public class TestController {
    /**
     * test1测试无统一返回结果时返回数据的形式和try...catch捕获异常
     *
     * @return
     */
    @RequestMapping("/test01")
    public JSONObject test1() {
        JSONObject result = new JSONObject();
        Map<String, Object> datamap = new HashMap<>();
        try {
            // 业务逻辑代码
            result.put("returnCode", "0000");
            result.put("returnInfo", "信息更新成功！");

            datamap.put("username", "wshy");
            datamap.put("password", "12345678");

            result.put("data", datamap);
        } catch (Exception e) {
            result.put("ReturnCode", "500");
            result.put("ReturnInfo", "系统异常，请联系管理员！");
        }
        return result;
    }

    /**
     * test2测试使用统一返回结果的形式
     *
     * @return
     */
    @RequestMapping("/test02")
    public ResponseInfo test2() {

        try {
            // 模拟异常业务代码
            int i = 1 / 0;
            Map<String, Object> datamap = new HashMap<>();
            datamap.put("username", "wshy");
            datamap.put("password", "12345678");
            return ResponseUtils.success(datamap);
        } catch (Exception e) {
            return ResponseUtils.failure();
        }
    }

    /**
     * test3测试使用统一异常处理,系统统一异常
     *
     * @return
     */
    @RequestMapping(value = "/test03")
    public ResponseInfo test(@RequestParam String userName, @RequestParam String password) {
        try {
            String data = "登录用户：" + userName + "，密码：" + password;
            return new ResponseInfo(data, "操作成功！");
        } catch (Exception e) {
            return new ResponseInfo("500", "系统异常，请联系管理员！");
        }
    }

    /**
     * test4测试使用自定义异常处理
     *
     * @return
     */
    @RequestMapping(value = "/test04")
    public ResponseInfo test04(@RequestParam String user) throws CenterErrorException {

        if ("wshy".equals(user)) {

            return ResponseUtils.success(user);
        } else {
            throw new CenterErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    /**
     * test6测试使用全局异常
     * pg1-如果浏览器输入内容为：http://localhost:8080/lay/test06?username=wshy,缺少password
     *      此时系统会抛出异常“Required String parameter 'password' is not present”，对捕获后的全局异常进行显示
     *      {"returnCode":"599","returnInfo":"Required String parameter 'password' is not present","data":null}
     *pg2-如果浏览器输入内容为：http://localhost:8080/lay/test06?username=wshy&password=111,用户名密码错误
     *      此时系统会抛出我们自定义的异常，“用户名错误或密码错误，请核实后登录！”
     *      {"returnCode":500,"returnInfo":"用户名错误或密码错误，请核实后登录！","data":null}
     * pg3-如果浏览器输入内容为：http://localhost:8080/lay/test06?username=wshy&password=123，正常状态
     *      此时系统相应正常
     *      {"returnCode":"0000","returnInfo":"请求成功！","data":{"password":"123","username":"wshy"}}
     * @return
     */
    @RequestMapping(value = "/test06")
    public ResponseInfo test06(@RequestParam String username, @RequestParam String password) throws CenterErrorException {
        if ("wshy".equals(username) && "123".equals(password)) {

            Map<String, Object> datamap = new HashMap<>();
            datamap.put("username", username);
            datamap.put("password", password);
            return ResponseUtils.success(datamap);
        }else {
            throw new CenterErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"用户名错误或密码错误，请核实后登录！");
        }
    }



    public void test07() {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBuffer2 = new StringBuffer();
        StringBuffer stringBuffer3 = new StringBuffer();


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    @RequestMapping("/test08")
    public ResponseInfo submitOrder(@RequestBody @Valid vertifyEntity entity, BindingResult error) throws CenterErrorException {

        if (error.hasErrors ()) {
            throw new CenterErrorException (HttpStatus.INTERNAL_SERVER_ERROR, error.getFieldError ().getDefaultMessage ().toString ());
        }

        Map <String, Object> datamap = new HashMap <> ();
        datamap.put ("username", entity.getName ());

        return ResponseUtils.success (datamap);

    }


}





