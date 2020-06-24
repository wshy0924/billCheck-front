package com.wshy.billcheck.Exception;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wshy
 * @data 2020/6/17
 **/
@Data
public class ResponseInfo<T> implements Serializable {

        private static final long serialVersionUID = -1914326003490641444L;
        /**
         * 返回码
         */
        protected Object returnCode;
        /**
         * 返回码描述
         */

        protected Object returnInfo;
        /**
         * 返回数据,不知道返回结果具体类型，这里使用泛型
         */
        private T data;

        /**
         * 无参构造函数，默认传入“0000”和“操作成功！”
         */
        public ResponseInfo() {
            this.returnCode = "0000";
            this.returnInfo = "操作成功！";
        }

        /**
         * @param data
         */
        public ResponseInfo (T data) {
            this.data = data;
        }

    /**
         * 构造函数，传入returncode和returninfo
         * @param returnCode
         * @param returnInfo
         */
        public ResponseInfo(Object returnCode,Object returnInfo) {
            this.returnCode = returnCode;
            this.returnInfo = returnInfo;
        }

        /**
         * 构造函数，传入returncode、returninfo和data
         * @param returnCode
         * @param returnInfo
         * @param data
         */
        public ResponseInfo(Object returnCode, Object returnInfo, T data) {
            this.returnCode = returnCode;
            this.returnInfo = returnInfo;
            this.data = data;
        }

}
