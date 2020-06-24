package com.wshy.billcheck.Utils;

/**
 * @author wshy
 * @data 2020/6/17
 **/
public class ParamUtils {
    /**
     * 获取异常信息
     * @param e
     * @return
     */
    public static String getError(Exception e) {
        if (e.getMessage() == null) {
            return "服务异常";
        } else if (e.getMessage().contains("Table 'geit_queue.queue_2019_08_25' doesn't exist") && e.getMessage().contains("doesn't exist")) {
            int start = e.getMessage().indexOf("Table");
            int end = e.getMessage().indexOf("doesn't exist");
            return e.getMessage().substring(start, end) + "不存在";
        } else if (e.getMessage().contains("Duplicate entry")) {
            return "添加了重复的记录";
        } else if (e.getMessage().contains("Data too long")) {
            return "参数长度过长";
        } else if (e.getMessage().contains("unique constraint")) {
            return "id字段重复，请检查";
        } else if (e.getMessage().contains("ORA-00904")) {
            return "无效列名";
        } else if (e.getMessage().contains("ORA-00942")) {
            return "表或者视图不存在";
        } else if (e.getMessage().contains("ORA-01400")) {
            return "不能将空值插入";
        } else if (e.getMessage().contains("ORA-00936")) {
            return "缺少表达式";
        } else if (e.getMessage().contains("ORA-00932")) {
            return "数据类型不一致";
        } else if (e.getMessage().contains("ORA-00933")) {
            return "SQL 命令未正确结束";
        } else if (e.getMessage().contains("ORA-01722")) {
            return "无效的数字赋值";
        } else if (e.getMessage().contains("ORA-06592")) {
            return "case语句格式有误";
        } else if (e.getMessage().contains("ORA-00001")) {
            return "主键唯一值冲突";
        } else if (e.getMessage().contains("ORA-00001")) {
            return "主键唯一值冲突，请检查id值";
        } else if (e.getMessage().contains("ORA-01422")) {
            return "返回超过一行";
        } else if (e.getMessage().contains("NullPointerException")) {
            return "空指针异常";
        } else if (e.getMessage().contains("ClassNotFoundException")) {
            return "类不存在";
        } else if (e.getMessage().contains("NumberFormatException")) {
            return "字符串转换为数字异常";
        } else if (e.getMessage().contains("IndexOutOfBoundsException")) {
            return "数组越界";
        } else if (e.getMessage().contains("IllegalArgumentException")) {
            return "方法的参数错误";
        } else {
            return e.getMessage().contains("NullPointerException") ? "空指针异常" : e.getMessage();
        }
    }

    /**
     * 获取异常状态码
     * @param e
     * @return
     */
    public static String getErrorCode(Exception e) {
        if (e.getMessage() == null) {
            return "500";
        } else if (e.getMessage().contains("Table") && e.getMessage().contains("doesn't exist")) {
            int start = e.getMessage().indexOf("Table");
            int end = e.getMessage().indexOf("doesn't exist");
            return "501";
        } else if (e.getMessage().contains("Duplicate entry")) {
            return "502";
        } else if (e.getMessage().contains("Data too long")) {
            return "503";
        } else if (e.getMessage().contains("unique constraint")) {
            return "504";
        } else if (e.getMessage().contains("ORA-00904")) {
            return "无效列名";
        } else if (e.getMessage().contains("ORA-00942")) {
            return "表或者视图不存在";
        } else if (e.getMessage().contains("ORA-01400")) {
            return "不能将空值插入";
        } else if (e.getMessage().contains("ORA-00936")) {
            return "缺少表达式";
        } else if (e.getMessage().contains("ORA-00932")) {
            return "数据类型不一致";
        } else if (e.getMessage().contains("ORA-00933")) {
            return "SQL 命令未正确结束";
        } else if (e.getMessage().contains("ORA-01722")) {
            return "无效的数字赋值";
        } else if (e.getMessage().contains("ORA-06592")) {
            return "case语句格式有误";
        } else if (e.getMessage().contains("ORA-00001")) {
            return "主键唯一值冲突";
        } else if (e.getMessage().contains("ORA-00001")) {
            return "主键唯一值冲突，请检查id值";
        } else if (e.getMessage().contains("ORA-01422")) {
            return "返回超过一行";
        } else if (e.getMessage().contains("NullPointerException")) {
            return "505";
        } else if (e.getMessage().contains("ClassNotFoundException")) {
            return "506";
        } else if (e.getMessage().contains("NumberFormatException")) {
            return "507";
        } else if (e.getMessage().contains("IndexOutOfBoundsException")) {
            return "508";
        } else {
            return e.getMessage().contains("IllegalArgumentException") ? "509" : "599";
        }
    }
}
