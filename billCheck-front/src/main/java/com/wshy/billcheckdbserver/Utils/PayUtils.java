package com.wshy.billcheckdbserver.Utils;

import com.wshy.billcheckdbserver.config.WXPayConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public class PayUtils {

        private final static Logger logger = LoggerFactory.getLogger(PayUtils.class);

        /**
         * 生成 MD5
         *
         * @param data
         *            待处理数据
         * @return MD5结果
         */
        public static String MD5(String data) throws Exception {
            java.security.MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        }

        /**
         * 生成订单流水号
         * 14位日期+6位随机数
         *
         * @return
         */
        public static String generatePipeline() {
            return DateUtils.getDateTime("yyyyMMdd") + DateUtils.getDateTime("HHmmss")
                    + (int) ((Math.random() * 9 + 1) * 100000);
        }

        /**
         * 国光支付宝网关签名计算
         *
         * @param data
         * @param key
         * @return
         * @throws Exception
         */
        public static String aligenerateSignature(Map <String, Object> data, String key) throws Exception {
            StringBuilder sb = new StringBuilder();
            sb.append(JsonUtils.MapToJson(data));
            sb.append(key);
            logger.info("aligenerateSignature sb = " + sb.toString());
            return MD5(sb.toString()).toUpperCase();
        }

        /**
         * 国光微信网关签名计算
         *
         * @param data
         * @param key
         * @return
         * @throws Exception
         */
        public static String wxgenerateSignature(Map<String, Object> data, String key) throws Exception {
            Set <String> keySet = data.keySet();
            String[] keyArray = keySet.toArray(new String[keySet.size()]);
            Arrays.sort(keyArray);
            StringBuilder sb = new StringBuilder();
            for (String k : keyArray) {
                if (k.equals(WXPayConstants.FIELD_SIGN)) {
                    continue;
                }
//			if (((String) data.get(k)).trim().length() > 0) // 参数值为空，则不参与签名
//				sb.append(k).append("=").append(((String) data.get(k)).trim()).append("&");
                sb.append(k).append("=").append(((String) data.get(k)).trim()).append("&");
            }
            sb.append("key=").append(key);
            logger.info("sb = " + sb.toString());
            return MD5(sb.toString()).toUpperCase();
        }

        /**
         * 在金额前面加上负号
         *
         * @param amt
         * @return
         */
        public static String minusAmt(String amt) {
            return "-" + amt;
        }

        public static String builderSignStr(Map<String, Object> params,String md5key) throws Exception {

            String paramsStr = JsonUtils.MapToJson(params);
            StringBuffer sb = new StringBuffer();
            sb.append(paramsStr);
            sb.append(md5key);
            logger.info("builderSignStr= " + sb.toString());
            logger.info("验证sign:" + MD5(sb.toString()).toUpperCase());
            return MD5(sb.toString()).toUpperCase();

        }


}
