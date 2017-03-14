package io.github.ray.xcache.serializer;

import com.alibaba.fastjson.JSON;

/**
 * FastJson序列化
 * @author linchanglei 2016年2月28日 下午4:24:36
 * @version V1.0   
 */
public class FastJsonSerializer implements Serializer {
	private static class FastJsonSerializerHolder {
		private static FastJsonSerializer INS = new FastJsonSerializer();
	}
	
	public static  Serializer getSerializer() {
		return FastJsonSerializerHolder.INS;
	}
	
	private FastJsonSerializer() {
	}
	
	public <T> String serialize(T t) {
		if(t instanceof String){
			return String.class.cast(t);
		}
		return JSON.toJSONString(t);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T deserialize(String v, Class<T> clazz) {
		if(String.class == clazz){
			return (T)v;
		}
		return JSON.parseObject(v, clazz);
	}
}
