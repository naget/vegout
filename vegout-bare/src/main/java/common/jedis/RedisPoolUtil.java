package common.jedis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author tianfeng
 */
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

    public static Long zadd(String key,String mem,Long score){
        Jedis jedis = null;
        Long result = null;
        try{
            jedis = RedisPool.getJedis();
            result = jedis.zadd(key,score,mem);
        }catch (Exception e){
            log.error("zadd异常,key:{},member:{},score:{}",key,mem,score);
        }finally {
            RedisPool.returnResource(jedis);
        }

        return result;
    }

    public static Long zremrangeByScore(String key,Long start,Long end){
        Jedis jedis = null;
        Long result = null;
        try{
            jedis = RedisPool.getJedis();
            result = jedis.zremrangeByScore(key,start,end);
        }catch (Exception e){
            log.error("zremrangeByScore异常,key:{},start:{},end:{}",key,start,end);
        }finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }
    public static int zrangeByScore(String key,Long start,Long end){
        Jedis jedis = null;
        int result = 0;
        try{
            jedis = RedisPool.getJedis();
            result = jedis.zrangeByScore(key,start,end).size();
        }catch (Exception e){
            log.error("zrangeByScore异常,key:{},start:{},end:{}",key,start,end);
        }finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    public static boolean limit(String key,Long count){
        Jedis jedis = null;
        boolean result = true;
        try{
            jedis = RedisPool.getJedis();
            long now = System.nanoTime();
            long oneSecondBefore = now - 60000;
            Transaction multi = jedis.multi();
            multi.zremrangeByScore(key,0,oneSecondBefore);
            if (multi.zrangeByScore(key,oneSecondBefore,now).get().size()<count){
                result = false;
                long uuid = UUID.randomUUID().node();
                multi.zadd(key, now,String.valueOf(uuid));
            }
            multi.exec();
        }catch (Exception e){
            log.error("limit jedis异常,key:{}",key,e);
        }finally {
            RedisPool.returnResource(jedis);
        }
        return result;
    }

    public static boolean luaLimit(String key,Long count){
        Jedis jedis = null;
        boolean result = true;
        try {
            jedis = RedisPool.getJedis();
            long now = System.currentTimeMillis();
            long oneSecondBefore = now - 60000;
            String script = "redis.call('zremrangeByScore', KEYS[1], 0, ARGV[1])\n" +
                    "local res = redis.call('zcard', KEYS[1]) print(res)\n" +
                    "if (res == nil) or (res < tonumber(ARGV[3])) then\n" +
                    "    print(\"+1\") redis.call('zadd', KEYS[1], ARGV[2], ARGV[4])\n" +
                    "    return 0\n" +
                    "else return 1 end";
            List<String> keys = new ArrayList<>();
            keys.add(key);
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(oneSecondBefore));
            values.add(String.valueOf(now));
            values.add(count.toString());
            values.add(UUID.randomUUID().toString());
            Object re = jedis.eval(script,keys,values);
            result = "1".equals(re.toString());
        }catch (Exception e){
            log.error("luaLimit error key:{}",key,e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }
    public static List<Long> lucker = new ArrayList<>();
    public static boolean luaLimit2(String key,Long count){
        Jedis jedis = null;
        boolean result = true;
        try {
            jedis = RedisPool.getJedis();
            long now = System.currentTimeMillis();
            long oneSecondBefore = now - 60000;
            String script = "redis.call('zremrangeByScore', KEYS[1], 0, ARGV[1])\n" +
                    "local res = redis.call('zcard', KEYS[1]) print(res)\n" +
                    "if (res == nil) or (res < tonumber(ARGV[3])) then\n" +
                    "    print(\"+1\") redis.call('zadd', KEYS[1], ARGV[2], ARGV[4])\n" +
                    "    return 0\n" +
                    "else return 1 end";
            List<String> keys = new ArrayList<>();
            keys.add(key);
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(oneSecondBefore));
            values.add(String.valueOf(now));
            values.add(count.toString());
            values.add(UUID.randomUUID().toString());
            Object re = jedis.eval(script,keys,values);
            result = "1".equals(re.toString());
            if (!result){
                lucker.add(now);
            }
        }catch (Exception e){
            log.error("luaLimit error key:{}",key,e);
            RedisPool.returnBrokenResource(jedis);
        }
        RedisPool.returnResource(jedis);
        return result;
    }
}
