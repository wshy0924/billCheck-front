package com.wshy.billcheck.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public class DateUtils {

        private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

        /**
         * 格式化时间
         *
         * @param format
         * @return
         */
        public static String getDateTime(String format) {
            Date now = new Date();
            SimpleDateFormat sd = new SimpleDateFormat(format);
            return sd.format(now);
        }

        /**
         * 格式化前一天
         *
         * @param format
         * @return
         */
        public static String getBeforeDateTime(String format) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1); // 得到前一天
            Date date = calendar.getTime();
            SimpleDateFormat sd = new SimpleDateFormat(format);
            return sd.format(date);
        }

        /**
         * 改变时间字符串的格式
         * @param strTime
         * @param srcformat
         * @param desformat
         * @return
         */
        public static String formatStringTime(String strTime,String srcformat,String desformat) {
            String now = "";
            try {
                Date date = new SimpleDateFormat(srcformat).parse(strTime);
                now = new SimpleDateFormat(desformat).format(date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return now;
        }

        public static void main(String[] args) {
            // TODO Auto-generated method stub
            // System.out.println(getDateTime("yyyy"));
//		 System.out.println(getDateTime("yyyyMMdd"));
            System.out.println(getDateTime("HHmmss"));
            // System.out.println(getDateTime("yyyy-MM-dd"));
            // System.out.println(getDateTime("yyyyMMddHHmmss"));
            // System.out.println(getDateTime("yyyy-MM-dd HH:mm:ss"));
//		System.out.println(formatStringTime("2019-07-18","yyyy-MM-dd","yyyyMMdd"));
//		System.out.println(formatStringTime("20190718","yyyyMMdd","yyyy-MM-dd"));
        }


}
