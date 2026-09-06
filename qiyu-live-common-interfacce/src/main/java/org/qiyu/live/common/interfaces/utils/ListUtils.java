package org.qiyu.live.common.interfaces.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {
    //把一个大的list集合拆成多个子list集合
    public static<T> List<List<T>> splistList(List<T> list,int subNum) {
        List<List<T>> resultList = new ArrayList<>();
        int priIndex = 0;
        int lastIndex = 0;
        int insertTimes = list.size() / subNum;
        List<T> subList;
        for (int i = 0; i <= insertTimes; i++) {
            priIndex = i * subNum;
            lastIndex = (i + 1) * subNum;
            if (i != insertTimes) {
                subList = list.subList(priIndex, lastIndex);
            }else{
                subList = list.subList(priIndex, list.size());
            }
            if (subList.size() > 0) {
                resultList.add(subList);
            }
        }
        return resultList;
    }
}
