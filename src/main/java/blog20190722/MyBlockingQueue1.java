package blog20190722;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * created by tianfeng on 2019/7/22
 * 阻塞队列-0.1版
 */
public class MyBlockingQueue1<T> implements MyBlockingQueue<T>{
    private Object[] array;
    private int count=0;
    private int getIndex=0;
    private int putIndex=0;

    private int putTimes = 0;//测试用
    private int takeTimes = 0;//测试用
    public MyBlockingQueue1(int cap){
        array = new Object[cap];
    }
    public synchronized void put(T ele) throws InterruptedException {
        while (isFull()){
            wait();
        }
        array[putIndex++]=ele;
        if (putIndex>=array.length){
            putIndex=0;
        }
        count++;
        notifyAll();
        putTimes++;
    }

    public synchronized T take() throws InterruptedException {
        while (isEmpty()){
            wait();
        }
        Object element = array[getIndex++];
        if (getIndex>=array.length){
            getIndex=0;
        }
        count--;
        notifyAll();
        @SuppressWarnings("unchecked")
        T t = (T)element;
        takeTimes++;
        return t;
    }
    private boolean isEmpty(){
        return count==0;
    }
    private boolean isFull(){
        return  count>=array.length;
    }

    public int getPutTimes() {
        return putTimes;
    }

    public int getTakeTimes() {
        return takeTimes;
    }
}
