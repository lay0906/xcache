package io.github.ray.xcache.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDK自带的序列化
 * @author linchanglei 2016年2月28日 下午4:24:48
 * @version V1.0   
 */
public class JdkSerializer implements Serializer{

	private static final Logger LOGGER = LoggerFactory.getLogger(JdkSerializer.class);
	
	private static class JdkSerializerHolder {
		private static JdkSerializer INS = new JdkSerializer();
	}
	
	public static  Serializer getSerializer() {
		return JdkSerializerHolder.INS;
	}
	
	private JdkSerializer() {
	}
	
	public <T> String serialize(T t) {
	    return toString(t);
    }

	public <T> T deserialize(String v, Class<T> clazz) {
	    Object o = toObject(v);
	    return clazz.cast(o);
    }
	
	private static String toString(Object o) {
		String serStr = "";
		if (null == o) {
			return serStr;
		}
		ByteArrayOutputStream byteArrayOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(o);
			serStr = byteArrayOutputStream.toString("ISO-8859-1");
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (objectOutputStream != null){
					objectOutputStream.close();
				}
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.close();
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return serStr;
	}
	
	private Object toObject(String s) {
		Object o = null;
		if (null == s || s.length() == 0) {
			return o;
		}
		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			byteArrayInputStream = new ByteArrayInputStream(s.getBytes("ISO-8859-1"));
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			o = objectInputStream.readObject();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				if (byteArrayInputStream != null){
					byteArrayInputStream.close();
				}
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return o;
	}
}
