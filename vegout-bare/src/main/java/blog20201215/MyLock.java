package blog20201215;

import common.jedis.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;

/**
 * @author tianfeng5
 */
@Slf4j
public class MyLock {
    public static int count = 0;
    private static int expireTime = 10;
    private static String lockKey = "desc";
    private static String lockValue = "lockValue";

    public void decr() {
        if (RedisPoolUtil.setNxPx(lockKey, lockValue, expireTime)) {
            try {
                if (count < 10) {
                    //业务处理...
                    Thread.sleep(10);
                    count++;
                }
                //模拟意外延迟
                if (System.currentTimeMillis() % 2 == 0) {
                    Thread.sleep(15);
                }
            } catch (Exception e) {
                log.info("操作error:{}", e.getMessage(), e);
            } finally {
                RedisPoolUtil.del(lockKey);
            }
        }
    }

    public void decr1() {
        if (RedisPoolUtil.setNxPx(lockKey, lockValue, expireTime)) {
            Thread t = null;
            try {
                t = daemon(Thread.currentThread());
                if (count < 10) {
                    //业务处理...
                    Thread.sleep(10);
                    count++;
                }
                //模拟意外延迟
                if (System.currentTimeMillis() % 2 == 0) {
                    Thread.sleep(15);
                }
            } catch (Exception e) {
                log.info("操作error:{}", e.getMessage(), e);
            } finally {
                if (Objects.nonNull(t)){
                    t.interrupt();
                }
                RedisPoolUtil.del(lockKey);
            }
        }
    }

    public void result() {
        log.info("拿到锁进行操作:{}",count);
    }

    public void decrV2() {
        if (RedisPoolUtil.setNxPx(lockKey, Thread.currentThread().getName(), expireTime)) {
            try {
                if (count < 10) {
                    //业务处理...
                    Thread.sleep(10);
                    count++;
                }
                //模拟意外延迟
                if (System.currentTimeMillis() % 2 == 0) {
                    Thread.sleep(15);
                }
            } catch (Exception e) {
                log.info("操作error:{}", e.getMessage(), e);
            } finally {
                String value = RedisPoolUtil.get(lockKey);
                if (!Thread.currentThread().getName().equals(value)) {
                    log.info("线程{}释放了{}的锁",Thread.currentThread().getName(),value);
                }
                RedisPoolUtil.del(lockKey);
            }
        }
    }

    public void decrV3() {
        if (RedisPoolUtil.setNxPx(lockKey, Thread.currentThread().getName(), expireTime)) {
            try {
                if (count < 10) {
                    //业务处理...
                    Thread.sleep(10);
                    count++;
                }
                //模拟意外延迟
                if (System.currentTimeMillis() % 2 == 0) {
                    Thread.sleep(15);
                }
            } catch (Exception e) {
                log.info("操作error:{}", e.getMessage(), e);
            } finally {
                String value = RedisPoolUtil.get(lockKey);
                if (!Thread.currentThread().getName().equals(value)) {
                    log.info("线程{}不可释放{}的锁",Thread.currentThread().getName(),value);
                }else {
                    RedisPoolUtil.del(lockKey);
                }
            }
        }
    }

    public void decrV4() {
        if (RedisPoolUtil.setNxPx(lockKey, Thread.currentThread().getName(), expireTime)) {
            try {
                if (count < 10) {
                    //业务处理...
                    Thread.sleep(10);
                    count++;
                }
                //模拟意外延迟
                if (System.currentTimeMillis() % 2 == 0) {
                    Thread.sleep(15);
                }
            } catch (Exception e) {
                log.info("操作error:{}", e.getMessage(), e);
            } finally {
                if (!RedisPoolUtil.releaseDistributedLock(lockKey,Thread.currentThread().getName())) {
                    log.info("线程{}不可释放{}的锁",Thread.currentThread().getName(), RedisPoolUtil.get(lockKey));
                }
            }
        }
    }

    public void decrV5() {
        Thread t = null;
        if (RedisPoolUtil.setNxPx(lockKey, Thread.currentThread().getName(), expireTime)) {
            try {
                 t = daemon(Thread.currentThread());
                if (count < 10) {
                    //业务处理...
                    Thread.sleep(10);
                    count++;
                }
                //模拟意外延迟
                if (System.currentTimeMillis() % 2 == 0) {
                    Thread.sleep(15);
                }
            } catch (Exception e) {
                log.info("操作error:{}", e.getMessage(), e);
            } finally {
                String value = RedisPoolUtil.get(lockKey);
                if (Objects.nonNull(t) && t.isAlive()){
                    t.interrupt();
                }
                if (!Thread.currentThread().getName().equals(value)) {
                    log.info("线程{}不可释放{}的锁",Thread.currentThread().getName(),value);
                }else {
                    RedisPoolUtil.del(lockKey);
                }
            }
        }
    }




    private Thread daemon(Thread parent){
        Thread t =  new Thread(new Runnable() {

            @Override
            public void run() {
                boolean end = false;
                do {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        log.info("{}终止了自己的辅助线程",parent.getName());
                        end = true;
                    }
                    if (!end){
                        log.info("给{}续命",parent.getName());
                        RedisPoolUtil.setEx(lockKey,parent.getName(),1);
                    }
                }while (parent.isAlive() && !end);
            }

        });
        t.start();
        return t;
    }


}
