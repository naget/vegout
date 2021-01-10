package blog20190722;

/**
 * created by tianfeng on 2019/7/25
 */
public interface MyBlockingQueue<T> {
    /**
     * 放入元素
     * @param t
     * @throws InterruptedException
     */
    void put(T t) throws InterruptedException;

    /**
     * 取元素
     * @return
     * @throws InterruptedException
     */
    T take()throws InterruptedException;

    int getTakeTimes();//测试用
    int getPutTimes();//测试用
}
