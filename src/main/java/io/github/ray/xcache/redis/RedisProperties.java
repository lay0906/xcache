package io.github.ray.xcache.redis;

import io.github.ray.xcache.exception.CacheInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RedisProperties {
	
	private static final Logger log = LoggerFactory.getLogger(RedisProperties.class);
	
	private static final String REDIS_PATH = "redis.properties";

	private static final String ADDRESSES = "redis.addresses";

	private static final String TEST_ON_BORROW = "redis.test_on_borrow";

	private static final String TEST_WHILE_IDLE = "redis.test_while_idle";
	
	private static final String TEST_ON_RETURN = "redis.test_on_return";

	private static final String MAX_IDLE = "redis.max_idle";

	private static final String MIN_IDLE = "redis.min_idle";

	private static final String MAX_ACTIVE = "redis.max_active";

	private static final String MAX_WAIT = "redis.max_wait";

	private static final String EVICT_TIME = "redis.evict_time";

	private static final String MIN_EVICT_TIME = "redis.min_evict_time";

	private static final String EVICT_NUMS = "redis.evict_nums";

	private static final String CONN_TIMEOUT = "redis.timeout";

	private static final String EXPIRE_TIME = "redis.expire_time";
	
	private static final Integer DEFAULT_EXPIRE_TIME = 1200;
	private static final Integer DEFAULT_CONNECTION_TIMEOUT = 10000;
	
	private static final String PASSWORD = "redis.password";
	
	private volatile static Integer expireTime = null;

	private static Properties redisProperties = null;

	private static final class RedisPropertiesHolder {
		private static final RedisProperties INSTANCE = new RedisProperties();
	}

	public static RedisProperties getInstance() {
		return RedisPropertiesHolder.INSTANCE;
	}

	private RedisProperties() {
		init();
	}

	private void init() {
		redisProperties = new Properties();
		InputStream is = null;
		is = RedisProperties.class.getClassLoader().getResourceAsStream(
				REDIS_PATH);
		try {
			redisProperties.load(is);
		} catch (Exception e) {
			log.error("读取{}失败！", REDIS_PATH);
			throw new CacheInitException("读取" + REDIS_PATH + "失败！", e);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
	
	public String getProperty(String key, String defaultValue) {
		return redisProperties.getProperty(key, defaultValue);
	}
	
	public String getProperty(String key) {
		return getProperty(key, null);
	}
	
	public static String getAddresses(){
		return getInstance().getProperty(ADDRESSES);
	}
	
	public static Boolean getTestOnBorrow(){
		String s = getInstance().getProperty(TEST_ON_BORROW);
		if(s == null){
			return null;
		}
		return Boolean.parseBoolean(s);
	}
	
	public static Boolean getTestWhileIdle(){
		String s = getInstance().getProperty(TEST_WHILE_IDLE);
		if(s == null){
			return null;
		}
		return Boolean.parseBoolean(s);
	}
	
	public static Boolean getTestOnReturn(){
		String s = getInstance().getProperty(TEST_ON_RETURN);
		if(s == null){
			return null;
		}
		return Boolean.parseBoolean(s);
	}
	
	public static Integer getMaxIdle(){
		String s = getInstance().getProperty(MAX_IDLE);
		if(s == null){
			return null;
		}
		try{
			return Integer.parseInt(s);
		}catch(Exception e){
			log.warn("解析redis.max_idle出错将使用默认值");
			return null;
		}
	}
	
	public static Integer getMinIdle(){
		String s = getInstance().getProperty(MIN_IDLE);
		if(s == null){
			return null;
		}
		try{
			return Integer.parseInt(s);
		}catch(Exception e){
			log.warn("解析redis.min_idle出错将使用默认值");
			return null;
		}
	}
	
	public static Integer getMaxActive(){
		String s = getInstance().getProperty(MAX_ACTIVE);
		if(s == null){
			return null;
		}
		try{
			return Integer.parseInt(s);
		}catch(Exception e){
			log.warn("解析redis.max_active出错将使用默认值");
			return null;
		}
	}
	
	public static Long getMaxWait(){
		String s = getInstance().getProperty(MAX_WAIT);
		if(s == null){
			return null;
		}
		try{
			return Long.parseLong(s);
		}catch(Exception e){
			log.warn("解析redis.max_wait出错将使用默认值");
			return null;
		}
	}
	
	public static Long getEvictTime(){
		String s = getInstance().getProperty(EVICT_TIME);
		if(s == null){
			return null;
		}
		try{
			return Long.parseLong(s);
		}catch(Exception e){
			log.warn("解析redis.evict_time出错将使用默认值");
			return null;
		}
	}
	
	public static Long getMinEvictTime(){
		String s = getInstance().getProperty(MIN_EVICT_TIME);
		if(s == null){
			return null;
		}
		try{
			return Long.parseLong(s);
		}catch(Exception e){
			log.warn("解析redis.min_evict_time出错将使用默认值");
			return null;
		}
	}
	
	public static Integer getEvictNums(){
		String s = getInstance().getProperty(EVICT_NUMS);
		if(s == null){
			return null;
		}
		try{
			return Integer.parseInt(s);
		}catch(Exception e){
			log.warn("解析redis.evict_nums出错将使用默认值");
			return null;
		}
	}
	
	public static Integer getConnectTimeout(){
		String s = getInstance().getProperty(CONN_TIMEOUT);
		if(s == null){
			log.warn("redis.timeout未配置将使用默认值[{}]", DEFAULT_CONNECTION_TIMEOUT);
			return DEFAULT_CONNECTION_TIMEOUT;
		}
		try{
			return Integer.parseInt(s);
		}catch(Exception e){
			log.warn("解析redis.timeout出错将使用默认值[{}]", DEFAULT_CONNECTION_TIMEOUT);
			return DEFAULT_CONNECTION_TIMEOUT;
		}
	}
	
	public static Integer getExpireTime(){
		if(expireTime == null){
			synchronized (RedisProperties.class) {
	            if(expireTime == null){
	            	String s = getInstance().getProperty(EXPIRE_TIME);
	        		if(s == null){
	        			expireTime =  DEFAULT_EXPIRE_TIME;
	        		}
	        		try{
	        			expireTime = Integer.parseInt(s);
	        		}catch(Exception e){
	        			log.warn("解析redis.expire_time出错将使用默认值");
	        			expireTime =  DEFAULT_EXPIRE_TIME;
	        		}
	            }
            }
		}
		return expireTime;
	}
	
	public static String getPassword(){
		return getInstance().getProperty(PASSWORD);
	}
}
