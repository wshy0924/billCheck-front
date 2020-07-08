package com.wshy.billcheckdbserver.serviceimpl;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/7/3
 **/
@Service
public class TOrderPayService {
    private final static Logger logger = LoggerFactory.getLogger (TOrderPayService.class);

    @Autowired
    private SqlSession sqlSession;


    public boolean insertAll(String stmt, Map <String, Object> paramMap) {
        int ret = Integer.valueOf(sqlSession.insert(stmt, paramMap));
        logger.info("ThirdTransService insertAll = " + ret);
        if (ret > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean insert(String stmt,Map<String, Object> paramMap) {
        int ret = Integer.valueOf(sqlSession.insert(stmt, paramMap));
        logger.info("----------OrderPayService insert----------");
        if(ret == 1)//成功
            return true;
        else
            return false;
    }

    public boolean update(String stmt,Map<String, Object> paramMap) {
        int ret = Integer.valueOf(sqlSession.update(stmt, paramMap));
        if(ret == 1)//成功
            return true;
        else
            return false;
    }
}
