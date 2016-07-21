/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 *
 * @author panjin
 * @version $Id: RandomList.java 2016年7月21日 下午5:11:44 $
 */
public class RandomList<T> extends ArrayList<T> {

    /**  */
    private static final long serialVersionUID = 69042797972318445L;
    
    private static final Random RANDOM         = new Random(System.currentTimeMillis());
    private int                 count          = 0;
    private int                 maxSize;

    public RandomList(int maxSize) {
        super(maxSize);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        ++count;
        if (count == Integer.MAX_VALUE) {
            return false;
        }
        if (count <= maxSize) {
            return super.add(t);
        }
        double p = (double) maxSize / (double) count;
        if (RANDOM.nextDouble() < p) {
            int i = RANDOM.nextInt(maxSize);
            this.set(i, t);
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        RandomList<Integer> randomList = new RandomList<Integer>(10);
        for (int i = 0; i < 1000; i++) {
            randomList.add(i);
        }
        System.out.println(randomList);
    }
}
