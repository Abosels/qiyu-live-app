package org.qiyu.live.common.interfaces.utils;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Bean 转换工具类
 */
public class ConvertBeanUtils {

    /**
     * 将一个对象转换成目标对象
     *
     * @param source      源对象
     * @param targetClass 目标对象 Class
     * @param <T>         目标对象类型
     * @return 目标对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }

        T target = newInstance(targetClass);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 将 List 对象转换成目标 List 对象
     *
     * @param sourceList  源对象集合
     * @param targetClass 目标对象 Class
     * @param <K>         源对象类型
     * @param <T>         目标对象类型
     * @return 目标对象集合
     */
    public static <K, T> List<T> convertList(List<K> sourceList, Class<T> targetClass) {
        if (sourceList == null) {
            return null;
        }

        List<T> targetList = new ArrayList<>((int) (sourceList.size() / 0.75) + 1);
        for (K source : sourceList) {
            targetList.add(convert(source, targetClass));
        }

        return targetList;
    }

    private static <T> T newInstance(Class<T> targetClass) {
        try {
            return targetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BeanInstantiationException(targetClass, "instantiation error", e);
        }
    }
}
