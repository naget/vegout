package blog20190722;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * created by tianfeng on 2019/7/25
 * 阻塞队列-0.2版
 */
public class MyBlockingQueue2<T> implements MyBlockingQueue<T>{

    private Object[] array;
    private AtomicInteger count=new AtomicInteger(0);//临界资源，使用原子变量类
    private int getIndex=0;
    private int putIndex=0;

    private  int putTimes = 0;//测试用
    private  int takeTimes = 0;//测试用
    private ReentrantLock putLock = new ReentrantLock();
    private final Condition notEmpty = putLock.newCondition();//防止过早唤醒
    private ReentrantLock takeLock = new ReentrantLock();
    private final Condition notFull = takeLock.newCondition();//防止过早唤醒

    public MyBlockingQueue2(int cap){
        array = new Object[cap];
    }
    public void put(T ele) throws InterruptedException {
        try {
            putLock.lock();
            while (isFull()){
                notFull.await();
            }
            array[putIndex++]=ele;
            if (putIndex>=array.length){
                putIndex=0;
            }
            int c = count.getAndIncrement();
            if (c==0){
                notEmpty.signal();
            }
        }finally {
            putLock.unlock();
            putTimes++;
        }

    }

    public  T take() throws InterruptedException {
        try {
            takeLock.lock();
            while (isEmpty()){
                notEmpty.wait();
            }
            Object element = array[getIndex++];
            if (getIndex>=array.length){
                getIndex=0;
            }
            int c = count.getAndDecrement();
            if (c==array.length){
                notFull.signal();
            }
            @SuppressWarnings("unchecked")
            T t = (T)element;
            return t;
        }finally {
            takeLock.unlock();
            takeTimes++;
        }

    }
    private boolean isEmpty(){
        return count.get()==0;
    }
    private boolean isFull(){
        return  count.get()>=array.length;
    }

    public int getPutTimes() {
        return putTimes;
    }

    public int getTakeTimes() {
        return takeTimes;
    }
}
