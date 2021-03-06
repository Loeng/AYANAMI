package com.whoiszxl.common;

import com.whoiszxl.utils.PropertiesUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis连接池
 * @author whoiszxl
 *
 */
public class RedisPool {

	/**
	 * jedis连接池
	 */
	private static JedisPool pool;
	/**
	 * 最大连接数
	 */
	private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));
	
	/**
	 * 在jedisPool中最大的idle状态（空闲）的jedis实例的个数
	 */
	private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));
	
	/**
	 * 在jedisPool中最小的idle状态（空闲）的jedis实例的个数
	 */
	private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","5"));

	/**
	 * 在borrow一个jedis实例时，是否要进行验证操作，
	 * 如果赋值为true，则得到的jedis实例是必定可以使用的
	 */
	private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));
	
	/**
	 * 在return一个jedis实例时，是否要进行验证操作，
	 * 如果赋值为true，则返回的jedis实例是必定可以使用的
	 */
	private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));
	
	/**
	 * redis ip地址
	 */
	private static String redisIp = PropertiesUtil.getProperty("redis.ip");
	
	/**
	 * redis 端口
	 */
	private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));
	
	/**
	 * redis 密码
	 */
	private static String redisPass = PropertiesUtil.getProperty("redis.password");
	
	private static void initPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);
		
		config.setBlockWhenExhausted(true);//连接耗尽时是否阻塞，false抛出异常，默认true
		
		pool = new JedisPool(config, redisIp, redisPort, 2*1000, redisPass);
	}
	
	static {
		System.out.println("初始化Redis连接池了哟");
		initPool();
	}
	
	public static Jedis getJedis() {
		return pool.getResource();
	}
	
	public static void returnResource(Jedis jedis) {
		if(jedis != null) {
			pool.returnResource(jedis);
		}
	}
	
	public static void returnBrokenResource(Jedis jedis) {
		if(jedis != null) {
			pool.returnBrokenResource(jedis);
		}
	}
	
	public static void main(String[] args) {
		Jedis jedis = pool.getResource();
		String string = jedis.get("girl");
		System.out.println(string);
		returnResource(jedis);
		pool.destroy();
		System.out.println("end");
	}
}
