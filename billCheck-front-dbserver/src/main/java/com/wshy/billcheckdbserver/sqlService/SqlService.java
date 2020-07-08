package com.wshy.billcheckdbserver.sqlService;

import com.alibaba.druid.sql.SQLUtils;
import com.wshy.billcheckdbserver.exception.CenterErrorException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author wshy
 * @data 2020/7/6
 **/
@Service
public class SqlService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${spring.datasource.driverClassName:mysql}")
    private String driverClassName;
    @Value("${file.url}")
    private String fileUrl;
    @Value("${file.uploadDir}")
    private String filePath;

    private Map arrayToMap(Map param, Object... args) {
        if (param == null) {
            param = new HashMap();
        }

        for(int i = 0; i < args.length; i += 2) {
            ((Map)param).put(args[i], args[i + 1]);
        }

        return (Map)param;
    }

    private String concatSqlid(String namespace, String sqlid) {
        return StringUtil.isNullOrEmpty(namespace) ? sqlid : namespace + "." + sqlid;
    }

    /*
    带args
     */
    public int insert(SqlSession sqlSession,String namespace, String sqlid,Object... args){
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid,this.arrayToMap((Map)null, args));
        this.logger.debug("sql:" + sql);
        int ret = sqlSession.insert (namespace + "." + sqlid,this.arrayToMap((Map)null, args));
        this.logger.debug("ret:" + ret);
        return ret;
    }
    /*
    带map
     */
    public int insert(SqlSession sqlSession,String namespace, String sqlid,Map param,Object... args){
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, this.arrayToMap((Map)param, args));
        this.logger.debug("sql:" + sql);
        int ret = sqlSession.insert (namespace + "." + sqlid,this.arrayToMap((Map)param, args));
        this.logger.debug("ret:" + ret);
        return ret;
    }

    public int delete(SqlSession sqlSession,String namespace, String sqlid,Map param){
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, param);
        this.logger.debug("sql:" + sql);
        int ret = sqlSession.delete (namespace + "." + sqlid,param);
        this.logger.debug("ret:" + ret);
        return ret;
    }


    public int update(SqlSession sqlSession, String namespace, String sqlid) {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, new HashMap ());
        this.logger.debug("sql:" + sql);
        int ret = sqlSession.update(namespace + "." + sqlid);
        this.logger.debug("ret:" + ret);
        return ret;
    }

    public int update(SqlSession sqlSession, String namespace, String sqlid, Object... args) {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, this.arrayToMap((Map)null, args));
        this.logger.debug("sql:" + sql);
        int ret = sqlSession.update(namespace + "." + sqlid, this.arrayToMap((Map)null, args));
        this.logger.debug("ret:" + ret);
        return ret;
    }

    public int update(SqlSession sqlSession, String namespace, String sqlid, Map param, Object... args) {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, this.arrayToMap(param, args));
        this.logger.debug("sql:" + sql);
        int ret = sqlSession.update(namespace + "." + sqlid, this.arrayToMap(param, args));
        this.logger.debug("ret:" + ret);
        return ret;
    }

    public List selectList(SqlSession sqlSession, String namespace, String sqlid) {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, new HashMap());
        this.logger.debug("sql:" + sql);
        List<Map> list = sqlSession.selectList(namespace + "." + sqlid);
        this.logger.debug("list size:" + list.size());
        this.logger.debug("list:" + list);
        return list;
    }

    public List selectList(SqlSession sqlSession, String namespace, String sqlid, Object... args) {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, this.arrayToMap((Map)null, args));
        this.logger.debug("sql:" + sql);
        List<Map> list = sqlSession.selectList(namespace + "." + sqlid, this.arrayToMap((Map)null, args));
        this.logger.debug("list size:" + list.size());
        this.logger.debug("list:" + list);
        return list;
    }

    public List selectList(SqlSession sqlSession, String namespace, String sqlid, Map param, Object... args) {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, this.arrayToMap(param, args));
        this.logger.debug("sql:" + sql);
        List<Map> list = sqlSession.selectList(namespace + "." + sqlid, this.arrayToMap(param, args));
        this.logger.debug("list size:" + list.size());
        this.logger.debug("list:" + list);
        return list;
    }

    public Object selectOne(SqlSession sqlSession, String namespace, String sqlid) {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, new HashMap());
        this.logger.debug("sql:" + sql);
        Object obj = sqlSession.selectOne(namespace + "." + sqlid);
        this.logger.debug("obj:" + obj);
        return obj;
    }

    public Object selectOne(SqlSession sqlSession, String namespace, String sqlid, Object... args) {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, this.arrayToMap((Map)null, args));
        this.logger.debug("sql:" + sql);
        Object obj = sqlSession.selectOne(namespace + "." + sqlid, this.arrayToMap((Map)null, args));
        this.logger.debug("obj:" + obj);
        return obj;
    }

    public Object selectOne(SqlSession sqlSession, String namespace, String sqlid, Map param, Object... args) {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, this.arrayToMap(param, args));
        this.logger.debug("sql:" + sql);
        Object obj = sqlSession.selectOne(namespace + "." + sqlid, this.arrayToMap(param, args));
        this.logger.debug("obj:" + obj);
        return obj;
    }

    public Object excuteSqlCheck(SqlSession sqlSession, String namespace, String sqlid, int size, String exception, Map paramMap, Object... args) throws CenterErrorException {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        paramMap = this.arrayToMap(paramMap, args);
        SqlCommandType sqlct = this.getSqlType(sqlSession, namespace, sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, paramMap);
        this.logger.debug("sql:" + sql);
        if (SqlCommandType.SELECT == sqlct) {
            List<Map> list = sqlSession.selectList(namespace + "." + sqlid, paramMap);
            this.logger.debug("obj: size:" + list.size() + "," + list);
            if (list.size() != size && !StringUtil.isNullOrEmpty(exception)) {
                throw new CenterErrorException (HttpStatus.INTERNAL_SERVER_ERROR, exception);
            } else {
                return list;
            }
        } else {
//            int ret = false;
            int ret = sqlSession.update(namespace + "." + sqlid, paramMap);
            this.logger.debug("obj: ret:" + ret);
            if (ret != size && !StringUtil.isNullOrEmpty(exception)) {
                throw new CenterErrorException (HttpStatus.INTERNAL_SERVER_ERROR, exception);
            } else {
                return ret;
            }
        }
    }

    public Object excuteSqlCheck(SqlSession sqlSession, String namespace, String sqlid, int size, String returnCode, String exception, Map paramMap, Object... args) throws  CenterErrorException {
        this.logger.debug("sqlid:" + namespace + "." + sqlid);
        paramMap = this.arrayToMap(paramMap, args);
        SqlCommandType sqlct = this.getSqlType(sqlSession, namespace, sqlid);
        String sql = this.getSql(sqlSession, namespace, sqlid, paramMap);
        this.logger.debug("sql:" + sql);
        if (SqlCommandType.SELECT == sqlct) {
            List<Map> list = sqlSession.selectList(namespace + "." + sqlid, paramMap);
            this.logger.debug("obj: size:" + list.size() + "," + list);
            if (list.size() != size && !StringUtil.isNullOrEmpty(exception)) {
                throw new CenterErrorException (returnCode, exception);
            } else {
                return list;
            }
        } else {
//            int ret = false;
            int ret = sqlSession.update(namespace + "." + sqlid, paramMap);
            this.logger.debug("obj: ret:" + ret);
            if (ret != size && !StringUtil.isNullOrEmpty(exception)) {
                throw new CenterErrorException (returnCode, exception);
            } else {
                return ret;
            }
        }
    }

    public SqlCommandType getSqlType(SqlSession sqlSession, String namespace, String sqlid) {
        MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(namespace + "." + sqlid);
        SqlCommandType sqlct = ms.getSqlCommandType();
        return sqlct;
    }


    public String getSql(SqlSession sqlSession, String namespace, String sqlid, Map paramMap) {
        MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(namespace + "." + sqlid);
        String sqlId = ms.getId();
        BoundSql boundSql = ms.getBoundSql(paramMap);
        Configuration configuration = ms.getConfiguration();
        String sql = this.getSql(configuration, boundSql, sqlId);
        String formatSql = SQLUtils.formatMySql(sql.substring(sql.indexOf(":") + 1));
        return formatSql;
    }

    private String getSql(Configuration configuration, BoundSql boundSql, String sqlId) {
        String sql = this.showSql(configuration, boundSql);
        StringBuilder str = new StringBuilder(100);
        str.append(sqlId);
        str.append(":");
        str.append(sql);
        return str.toString();
    }

    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(2, 2, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else if (obj != null) {
            value = obj.toString();
        } else {
            value = "";
        }

        return value;
    }

    private String showSql(Configuration configuration, BoundSql boundSql) {
        Object parameterObject = boundSql.getParameterObject();
        List <ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (CollectionUtils.isNotEmpty(parameterMappings) && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                Iterator var8 = parameterMappings.iterator();

                while(var8.hasNext()) {
                    ParameterMapping parameterMapping = (ParameterMapping)var8.next();
                    String propertyName = parameterMapping.getProperty();
                    Object obj;
                    if (metaObject.hasGetter(propertyName)) {
                        obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        sql = sql.replaceFirst("\\?", "缺失");
                    }
                }
            }
        }

        return sql;
    }
}
