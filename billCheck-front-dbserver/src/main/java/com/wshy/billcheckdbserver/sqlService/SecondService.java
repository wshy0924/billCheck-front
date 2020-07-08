package com.wshy.billcheckdbserver.sqlService;

import com.wshy.billcheckdbserver.exception.CenterErrorException;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/7/6
 **/
@Service
@Transactional
public class SecondService implements SqlInterface {
    private final static Logger logger = LoggerFactory.getLogger (SecondService.class);

    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private SqlService sqlService;

    @Override
    public Boolean update (String namespace, String sqlid) {
        int ret  = this.sqlService.update (this.sqlSession,namespace,sqlid);
        if (ret >= 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int update (String namespace, String sqlid, Object... args) {
        return this.sqlService.update (this.sqlSession,namespace,sqlid,args);
    }

    @Override
    public int update (String namespace, String sqlid, Map param, Object... args) {
        return this.sqlService.update (this.sqlSession,namespace,sqlid,param,args);

    }

    @Override
    public List selectList (String namespace, String sqlid) {
        return this.sqlService.selectList (this.sqlSession,namespace,sqlid);
    }

    @Override
    public List selectList (String namespace, String sqlid, Object... args) {
        return this.sqlService.selectList (this.sqlSession,namespace,sqlid,args);
    }

    @Override
    public List selectList (String namespace, String sqlid, Map param, Object... args) {
        return this.sqlService.selectList (this.sqlSession,namespace,sqlid,param,args);
    }

    @Override
    public Object selectOne (String namespace, String sqlid) {
        return this.sqlService.selectOne (this.sqlSession,namespace,sqlid);
    }

    @Override
    public Object selectOne (String namespace, String sqlid, Object... args) {
        return this.sqlService.selectOne (this.sqlSession,namespace,sqlid,args);
    }

    @Override
    public Object selectOne (String namespace, String sqlid, Map param, Object... args) {
        return this.sqlService.selectOne (this.sqlSession,namespace,sqlid,param,args);
    }

    @Override
    public Object excuteSql (String namespace, String sqlid) throws CenterErrorException {
        return null;
    }

    @Override
    public Object excuteSql (String namespace, String sqlid, Object... args) throws CenterErrorException {
        return null;
    }

    @Override
    public Object excuteSql (String namespace, String sqlid, Map paramMap, Object... args) throws CenterErrorException {
       return null;
    }

    @Override
    public Object excuteSqlCheck (String namespace, String sqlid, int size, String exception, Map paramMap, Object... args) throws CenterErrorException {
        return this.sqlService.excuteSqlCheck (this.sqlSession,namespace,sqlid,size,exception,paramMap,args);
    }

    @Override
    public Object excuteSqlCheck (String namespace, String sqlid, int size, String returnCode, String exception, Map paramMap, Object... args) throws CenterErrorException {
        return this.sqlService.excuteSqlCheck (this.sqlSession,namespace,sqlid,size,returnCode,exception,paramMap,args);
    }

    @Override
    public Object process (String namespace, String sqlid) throws CenterErrorException {
        return null;
    }

    @Override
    public Object process (String namespace, String sqlid, Object... args) throws CenterErrorException {
        return null;
    }

    @Override
    public Object process (String namespace, String sqlid, Map param, Object... args) throws CenterErrorException {
        return null;
    }

    @Override
    public Object insert (String namespace, String sqlid, Object... args) throws CenterErrorException {
        int ret = this.sqlService.insert (this.sqlSession,namespace,sqlid,args);
        logger.info(namespace + " insert ret = " + ret);
        if (ret > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object insert (String namespace, String sqlid, Map param, Object... args) throws CenterErrorException {
        int ret = this.sqlService.insert (this.sqlSession,namespace,sqlid,param,args);
        logger.info(namespace + " insert ret = " + ret);
        if (ret > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object delete (String namespace, String sqlid, Map param) throws CenterErrorException {

        int ret = this.sqlService.delete (this.sqlSession,namespace,sqlid,param);
        logger.info(namespace + " delete ret = " + ret);
        if (ret >= 0) {
            return true;
        } else {
            return false;
        }
    }
}
