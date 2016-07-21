/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters.support;

import com.panjin.backend.trace.meta.model.Annotation;

/**
 *
 *
 * @author panjin
 * @version $Id: Spans.java 2016年7月21日 下午6:04:39 $
 */
public abstract class Spans {

    /**
     * 生成span
     * 
     * @param type
     * @param endpoint
     * @param start
     * @return
     */
    public static Annotation genAnnotation(Annotation.AnnType type, long start) {
        Annotation annotation = new Annotation();
        annotation.setValue(type);
        annotation.setTimestamp(start);
        // annotation.setSize(size);
        return annotation;
    }
}
