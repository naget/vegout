package blog20190722.JDKBlockingQueueTest;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * created by tianfeng on 2019/7/26
 */
@Slf4j
public class BlockingQueueTest {
    public static void main(String[] args) {
        TestHandler testHandler = new TestHandler();
        final BlockingQueue<Integer> queue = (BlockingQueue<Integer>) testHandler.newProxyInstance(new ArrayBlockingQueue<Integer>(10));
//        final BlockingQueue<Integer> queue = (BlockingQueue<Integer>) testHandler.newProxyInstance(new LinkedBlockingQueue<Integer>(10));

        ExecutorService putservice = Executors.newCachedThreadPool();
        ExecutorService getservice = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i=0;i<5000;i++){
            putservice.submit(new Runnable() {
                public void run() {
                    try {
                        countDownLatch.await();
                        queue.put((int) (Math.random()*10));
                    } catch (InterruptedException e) {

                    }
                }
            });
            getservice.submit(new Runnable() {
                public void run() {
                    try {
                        countDownLatch.await();
                        queue.take();
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
        countDownLatch.countDown();
        putservice.shutdown();
        getservice.shutdown();
        log.info("takeTimes:"+testHandler.takeTimes+ " putTimes:"+testHandler.putTimes);
    }
}
