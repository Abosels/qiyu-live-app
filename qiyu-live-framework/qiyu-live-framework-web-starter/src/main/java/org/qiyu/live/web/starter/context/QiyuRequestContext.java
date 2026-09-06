package org.qiyu.live.web.starter.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户请求上下文
 */
public class QiyuRequestContext {
    private static ThreadLocal<Map<Object,Object>> resources = new InheritableThreadLocal<>();

    public static Long getUserId(){
        Object userId = get(RequestConstants.QIYU_USER_ID);
        return userId==null?null:Long.valueOf(userId.toString());
    }

    //设计一个set
    public static void set(Object key, Object value) {
        if(key == null ){
            throw new IllegalArgumentException("key is null");
        }
        // 确保 ThreadLocal 已初始化，兼容 InheritableThreadLocal 在某些 JVM/场景下 get() 返回 null 的情况
        Map<Object, Object> map = resources.get();
        if (map == null) {
            map = new HashMap<>();
            resources.set(map);
        }
        if(value == null){
            map.remove(value);
        } else {
            map.put(key, value);
        }
    }
    //设计一个get
    public static Object get(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        Map<Object, Object> map = resources.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }
    //设计一个clear方法， 防止内存泄露， sprigboot-web容器处理请求，tomcat工作线程会去处理我们的业务请求，工作线程是会长时间存在的
    public static void clear() {
        resources.remove();
    }
    //实现父子线程之间的本地变量传递
    //A--> threadLocal("userId" ,1001)
    //B--> new Thread(B) -->B线程属于A线程的子线程, 通过 threadLocal get("userId")也能得到1001
    private static final class InheritableThreadLocalMap<T extends Map<Object,Object>> extends InheritableThreadLocal<Map<Object,Object>> {
        @Override
        protected Map<Object, Object> initialValue() {
            return new HashMap();
        }

        @Override
        protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
            if (parentValue != null) {
                return (Map<Object, Object>) ((HashMap<Object,Object>) parentValue).clone();
            }else {
                return null;
            }
        }
    }
}
