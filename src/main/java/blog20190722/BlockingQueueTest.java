package blog20190722;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.*;

/**
 * created by tianfeng on 2019/7/25
 */
@Slf4j
public class BlockingQueueTest {


    public static void main(String[] args) {
        final MyBlockingQueue<Integer> queue = new MyBlockingQueue1<Integer>(10);
//        final BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(10)
        ExecutorService putService = Executors.newCachedThreadPool();
        ExecutorService takeService = Executors.newCachedThreadPool();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i=0;i<5000;i++){
            putService.submit(new Runnable() {
                public void run() {
                    try {
                        countDownLatch.await();
                        queue.put((int)(Math.random()*10));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            takeService.submit(new Runnable() {
                public void run() {
                    try {
                        countDownLatch.await();
                        queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        countDownLatch.countDown();
        putService.shutdown();
        takeService.shutdown();
        log.info("===========queue==============\ntaketimes:"+(queue).getTakeTimes()+" puttimes:"+(queue).getPutTimes());
    }
}
