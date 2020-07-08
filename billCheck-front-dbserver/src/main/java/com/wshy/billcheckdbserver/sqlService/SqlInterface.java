package com.wshy.billcheckdbserver.sqlService;

import com.wshy.billcheckdbserver.exception.CenterErrorException;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.util.List;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/7/6
 **/
public interface SqlInterface {
    Boolean update(String namespace, String sqlid);

    Boolean update(String namespace, String sqlid, Object... args);

    Boolean update(String namespace, String sqlid, Map param, Object... args);

    List selectList(String namespace, String sqlid);

    List selectList(String namespace, String sqlid, Object... args);

    List selectList(String namespace, String sqlid, Map param, Object... args);

    Object selectOne(String namespace, String sqlid);

    Object selectOne(String namespace, String sqlid, Object... args);

    Object selectOne(String namespace, String sqlid, Map param, Object... args);

    Object excuteSql(String namespace, String sqlid) throws CenterErrorException;

    Object excuteSql(String namespace, String sqlid, Object... args) throws CenterErrorException;

    Object excuteSql(String namespace, String sqlid, Map paramMap, Object... args) throws CenterErrorException;

    Object excuteSqlCheck(String namespace, String sqlid, int size, String exception, Map paramMap, Object... args) throws CenterErrorException;

    Object excuteSqlCheck(String namespace, String sqlid, int size, String returnCode, String exception, Map paramMap, Object... args) throws CenterErrorException;

    Object process(String namespace, String sqlid) throws CenterErrorException;

    Object process(String namespace, String sqlid, Object... args) throws CenterErrorException;

    Object process(String namespace, String sqlid, Map param, Object... args) throws CenterErrorException;

    Object insert(String namespace,String sqlid,Object...args) throws CenterErrorException;

    Object insert(String namespace,String sqlid,Map param,Object...args) throws CenterErrorException;

    Object delete(String namespace,String sqlid,Map param) throws CenterErrorException;

}
