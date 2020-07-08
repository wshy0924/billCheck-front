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
 * @data 2020/6/29
 **/
@Service
public class HisTransService {
    private final static Logger logger = LoggerFactory.getLogger(HisTransService.class);

    @Autowired
    private SqlSession sqlSession;

    /**
     * 将临时表数据全部导入正式表
     * 需要事务控制回滚
     * @param stmt
     * @param paramMap
     * @return
     */
    public boolean insertAll(String stmt, Map <String, Object> paramMap) {
        int ret = Integer.valueOf(sqlSession.insert(stmt, paramMap));
        logger.info("ThirdTransService insertAll = " + ret);
        if (ret > 0) {
            return true;
        } else {
            return false;
        }
    }

    public List <Map> selectAll(String stmt, Map paramMap){
        List<Map> thirdtransinfoList = sqlSession.selectList(stmt, paramMap);
        return thirdtransinfoList;
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
