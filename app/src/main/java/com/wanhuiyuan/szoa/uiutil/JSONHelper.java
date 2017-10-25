package com.wanhuiyuan.szoa.uiutil;

import java.util.List;

import com.alibaba.fastjson.JSON;
/**
 * 描述:JSONDom4j解析工具类
 * **/
public class JSONHelper {
	/**
	 * 描述:转换成短整型方法
	 * @param	json:	json文本
	 * @return	Integer:短整型对象
	 * **/
	public static boolean convertToBoolean(String json){
		
		return Boolean.valueOf(String.valueOf(JSON.parse(json))).booleanValue();
	}
	
	
	/**
	 * 描述:转换成短整型方法
	 * @param	json:	json文本
	 * @return	Integer:短整型对象
	 * **/
	public static Short convertToShort(String json){
		
		return Short.valueOf(String.valueOf(JSON.parse(json)));
	}
	/**
	 * 描述:转换成整型方法
	 * @param	json:	json文本
	 * @return	Integer:整型对象
	 * **/
	public static Integer convertToInteger(String json){
		
		return Integer.valueOf(String.valueOf(JSON.parse(json)));
	}
	/**
	 * 描述:转换成长整型方法
	 * @param	json:	json文本
	 * @return	Integer:长整型对象
	 * **/
	public static Long convertToLong(String json){
		
		return Long.valueOf(String.valueOf(JSON.parse(json)));
	}
	/**
	 * 描述:转换成浮点类型方法
	 * @param	json:	json文本
	 * @return	Integer:浮点类型对象
	 * **/
	public static Float convertToFloat(String json){
		
		return Float.valueOf(String.valueOf(JSON.parse(json)));
	}
	/**
	 * 描述:转换成双精度浮点类型方法
	 * @param	json:	json文本
	 * @return	Integer:双精度浮点类型对象
	 * **/
	public static Double convertToDouble(String json){
		return Double.valueOf(String.valueOf(JSON.parse(json)));
	}
	/**
	 * 描述:转换成对象方法
	 * @param	json:	json文本
	 * @return	Object:对象
	 * **/
	public static String convertToString(String json){
		return String.valueOf(JSON.parse(json));
	}
	/**
	 * 描述:转换成对象方法
	 * @param	json:	json文本
	 * @return	Object:对象
	 * **/
	public static Object convertToObject(String json){
		return JSON.parse(json);
	}
	/**
	 * 描述:转换成对象方法
	 * @param	json:		json文本
	 * @param	objClass:	自定义类的Class
	 * @return	Object:对象
	 * **/
	public static <T> Object convertToObject(String json,Class<T> objClass){
		
		return JSON.parseObject(json, objClass);
	}		
	
	/**
	 * 描述:转换成JSON数据类型方法
	 * @param	obj:	实体对象
	 * @return	JSON:	json类型文本数据
	 * **/
	public static String convertToJSON(Object obj){
		return JSON.toJSONString(obj);
	}
	/**
	 * 描述:转换成列表集合方法
	 * @param	json:		json文本
	 * @param	objClass:	自定义类的Class
	 * @return	List<T>:数组对象
	   json : name, age
	   person: name, age, sex
	 * **/
	
	public static <T> List<T>  convertToList(String json,Class<T> objClass){
		
		return JSON.parseArray(json, objClass);
	}
}
