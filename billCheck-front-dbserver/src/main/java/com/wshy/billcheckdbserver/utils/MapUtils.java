package com.wshy.billcheckdbserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MapUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(MapUtils.class);
	
	/**
	 * 获取Map的Key值
	 * @param paramMap
	 * @param listKey
	 * @param defVal
	 * @return
	 */
	public static String getMapParamValue(Map paramMap, String listKey, String defVal) {
		if (paramMap.get(listKey) != null) {
			return (String) paramMap.get(listKey);
		} else {
			return defVal;
		}
	}
	
	/**
	 * 删除Map空值
	 * @param paramMap
	 * @return
	 */
	public static Map<String, String> removeMapEmptyValue(Map<String, String> paramMap) {
		Set<String> set = paramMap.keySet();
		Iterator<String> it = set.iterator();
		List<String> listKey = new ArrayList<String>();
		while (it.hasNext()) {
			String str = it.next();
			if (paramMap.get(str) == null || "".equals(paramMap.get(str))) {
				listKey.add(str);
			}
		}
		for (String key : listKey) {
			paramMap.remove(key);
		}
		return paramMap;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
