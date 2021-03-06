package common.utils;

import blog20201215.*;
import blog20210109.Limiter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author tianfeng
 */
@Slf4j
public class ConcurrentTestUtil {
    //请求总数
    public static int clientTotal=5000;
    //同时并发执行的线程数
    public static MyLock myLock = new MyLock();
    private static Limiter limiter = new Limiter();
    public static void main(String []args) throws InterruptedException {

        //定义线程池
        ExecutorService executorService= new ThreadPoolExecutor(100, 100, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        //定义一个计数递减锁
        final CountDownLatch countDownLatch=new CountDownLatch(clientTotal);
        for(int i=0;i<clientTotal;i++) {
            executorService.execute(() -> {
                try {
                    doSomething();
                    countDownLatch.countDown();
                } catch (Exception e) {
                    log.error("exception",e);
                }
            });
        }
        countDownLatch.await();
        executorService.shutdown();;
        limiter.result();
    }

    private static void doSomething(){
    limiter.limitTest(10L);
        }
}
