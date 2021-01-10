package blog20190722.JDKBlockingQueueTest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * created by tianfeng on 2019/7/26
 */
public class TestHandler implements InvocationHandler {
    private Object target;
    public int takeTimes = 0;
    public int putTimes = 0;
    public Object newProxyInstance(Object target){
        this.target = target;
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),this);

    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object r = method.invoke(target,args);
        if (method.getName().equalsIgnoreCase("take"))
            takeTimes++;
        else if (method.getName().equalsIgnoreCase("put"))
            putTimes++;
        return r;
    }
}
