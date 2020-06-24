package com.wshy.billcheck.Utils;

import com.wshy.billcheck.Exception.CenterErrorException;
import com.wshy.billcheck.Exception.ResponseMsgEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/24
 **/
public class test {
    public static void vertifyParam(Map <String,Object> map, List keyList) throws CenterErrorException {
        //遍历map中的key,并进行校验
        for (String key : map.keySet ()) {
            for (int i = 0; i < keyList.size () ; i++) {

                if (!map.containsKey (keyList.get (i))) {          //key不存在抛异常，key存在则判断该key对应的value值是否为空
                    throw new CenterErrorException (ResponseMsgEnum.PARMETER_EXCEPTION, keyList.get (i) + "为必传项！");
                }else if ("".equals (map.get (keyList.get (i)))){
                    throw new CenterErrorException (ResponseMsgEnum.PARMETER_EXCEPTION, keyList.get (i) + "不允许为空！");
                }
            }
        }
    }

    public static void main (String[] args) throws CenterErrorException {
        Map<String,Object> map = new HashMap <> ();
        List keyList = new ArrayList ();
        keyList.add ("key1");
        keyList.add ("key2");
        keyList.add ("key3");
        keyList.add ("key4");
        keyList.add ("key5");
        keyList.add ("key6");

        map.put ("key1","value1");
        map.put ("key2","11");
        map.put ("key3","value3");
        map.put ("key4","value4");
        map.put ("key5","value5");

        vertifyParam (map,keyList);

        System.out.println ("111");

    }
}
