package com.wshy.billcheckdbserver.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public class JsonUtils {

        private final static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

        /**
         * map转json
         *
         * @param data
         * @return
         */
        public static String MapToJson(Map <String, Object> data) {
            ObjectMapper mapper = new ObjectMapper();
            String mjson = null;
            try {
                mjson = mapper.writeValueAsString(data);
                logger.info("mjson = " + mjson);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return mjson;
        }

        /**
         * json转map
         *
         * @param json
         * @return
         */
        public static Map<String, Object> JsonToMapObj(String json) {
            ObjectMapper mapper = new ObjectMapper();
            //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            Map<String, Object> map = new HashMap <String, Object>();
            try {
                map = mapper.readValue(json, Map.class);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return map;
        }

        public static void main(String[] args) {
            // TODO Auto-generated method stub

        }



}
