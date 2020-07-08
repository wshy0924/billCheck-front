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
public class TOrderService {
    private final static Logger logger = LoggerFactory.getLogger (TOrderService.class);

    @Autowired
    private SqlSession sqlSession;

    /**
     * 可以在这个服务里处理多个SQL，加上事务
     * @param paramMap
     * @return
     */
    public Object selectService(Map paramMap) {
        String tradeType = (String) paramMap.get("tradeType");
        logger.info("tradeType = " + tradeType);
        return selectOrder(tradeType,paramMap);
    }

    public List <Map> selectOrder(String stmt, Map paramMap){
        List<Map> orderinfoList = sqlSession.selectList(stmt, paramMap);
        return orderinfoList;
    }

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

    public boolean delete(String stmt, Map<String, Object> paramMap) {
        int ret = Integer.valueOf(sqlSession.delete(stmt, paramMap));
        logger.info("ThirdTransService delete ret = " + ret);
        if (ret >= 0) {
            return true;
        } else {
            return false;
        }
    }
}
