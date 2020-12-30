package common.jedis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class RedisPoolUtil {

    /**
     * 设置key的有效期，单位是秒
     *
     * @param key
     * @param exTime
     * @return
     */
    public static Long expire(String key, int exTime) {
        Jedis jedis = null;
        Long result = null;
        try {
            //从Redis连接池中获得Jedis对象
            jedis = RedisPool.getJedis();
            //设置成功则返回Jedis对象
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    //exTime的单位是秒
    //设置key-value并且设置超时时间
    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("setEX error key:{}, value:{}",key,value,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }
    public static boolean setNxEx(String key, String value, long time){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key,value,"NX","EX",time);
            log.info("setNxEx，key:{},value:{},redis返回结果：{}",key,value,result);
        } catch (Exception e) {
            log.error("setNxEx key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return false;
        }
        RedisPool.returnResource(jedis);
        return "OK".equals(result);
    }

    public static boolean setNxPx(String key, String value, long time){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key,value,"NX","PX",time);
//            log.info("setNxPx，key:{},value:{},redis返回结果：{}",key,value,result);
        } catch (Exception e) {
            log.error("setNxEx key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return false;
        }
        RedisPool.returnResource(jedis);
        return "OK".equals(result);
    }


    public static boolean releaseDistributedLock(String lockKey, String value) {
        Jedis jedis = null;
        Object result;
        try{
            jedis = RedisPool.getJedis();
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(value));
        }catch (Exception e) {
            log.error("releaseDistributedLock error key:{},value:{}", lockKey, value, e);
            RedisPool.returnBrokenResource(jedis);
            return false;
        }
        RedisPool.returnResource(jedis);
        if ("OK".equals(result)) {
            return true;
        }
        return false;
    }
}
