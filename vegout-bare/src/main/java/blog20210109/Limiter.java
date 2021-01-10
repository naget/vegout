package blog20210109;

import common.jedis.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流器测试
 * @author tianfeng
 */
@Slf4j
public class Limiter {
    private static AtomicInteger sum = new AtomicInteger(0);

    /**
     * 并发调用该方法，检测限流是否生效
     */
    public void limitTest(Long count){
        if (!RedisPoolUtil.luaLimit("gatewayLimit",count)){
            log.info("进入");
            sum.incrementAndGet();
        }
    }

    public void result(){
        log.info("一分钟内进入的请求数有:{}",sum.get());
    }
}
