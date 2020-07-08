package com.wshy.billcheckdbserver.sqlService;

import java.util.regex.Pattern;

/**
 * @author wshy
 * @data 2020/7/6
 **/
public class StringUtil {
    public StringUtil() {
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty() || str.isEmpty();
    }

    public static boolean startsWith(String str, String regexp) {
        return Pattern.compile(regexp).matcher(str).find();
    }

    public static void main(String[] args) {
        System.out.println(stringToAscii(" {\"transCode\":\"0001\",\"indCode\":\"8410\",\"chanelType\":\"01\",\"merchantId\":\"000001\",\"operId\":\"\",\"termId\":\"0010010001\",\"depart\":\"1234\",\"orderType\":\"8410001\",\"payType\":\"0102\",\"ybFlag\":\"1\",\"orderAmt\":\"2\",\"settleAmt\":\"1\",\"buyerName\":\"abc\",\"buyerTel\":\"12345678901\"}"));
        System.out.println(stringToAscii("{\n    \"transCode\":\"0001\",\n    \"indCode\":\"8410\",\n    \"chanelType\":\"01\",\n    \"merchantId\":\"000001\",\n    \"operId\":\"\",\n    \"termId\":\"0010010001\",\n    \"depart\":\"1234\",\n    \"orderType\":\"8410001\",\n    \"payType\":\"0102\",\n    \"ybFlag\":\"1\",\n    \"orderAmt\":\"2\",\n    \"settleAmt\":\"1\",\n    \"buyerName\":\"abc\",\n    \"buyerTel\":\"12345678901\"\n}"));
    }

    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();

        for(int i = 0; i < chars.length; ++i) {
            if (i != chars.length - 1) {
                sbu.append(chars[i]).append(",");
            } else {
                sbu.append(chars[i]);
            }
        }

        return sbu.toString();
    }
}
