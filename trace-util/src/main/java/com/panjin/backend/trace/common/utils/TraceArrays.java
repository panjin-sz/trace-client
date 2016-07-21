/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceArrays.java 2016年7月21日 下午5:17:02 $
 */
public class TraceArrays {

    /**
     * array is empty
     * 
     * @param fields
     * @return
     */
    public static boolean isEmpty(Field[] fields) {
        return fields == null || fields.length == 0;
    }

    /**
     * 对集合进行截取
     *
     * @param tSet
     * @param size
     * @param <T>
     * @return
     */
    public static <T> TreeSet<T> subSet(TreeSet<T> tSet, int size) {
        if (null == tSet || tSet.size() == 0 || size == 0) {
            return tSet;
        }

        if (tSet.size() <= size) {
            return tSet;
        }

        TreeSet<T> set = new TreeSet<T>();
        int i = 0;
        for (T o : tSet) {
            set.add(o);
            i++;
            if (i >= size) {
                break;
            }
        }
        return set;
    }

    public static void main(String[] args) {
        TreeSet tSet = Sets.newTreeSet();
        tSet.add(1);
        tSet.add(2);
        tSet.add(3);
        tSet.add(14);
        tSet.add(12);

        Set<Integer> objects = TraceArrays.subSet(tSet, 2);

        System.out.println(objects.size());
    }

}
