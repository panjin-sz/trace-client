/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters;

import com.panjin.backend.trace.meta.model.Span;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceContext.java 2016年7月21日 下午5:47:51 $
 */
public class TraceContext {

    private boolean isRoot; // 是否根路径

    private boolean isSmaple;

    private Span span;

    public TraceContext() {
    }

    public static void main(String[] args) {
    }

    public Span getSpan() {
        return span;
    }

    public void setSpan(Span span) {
        this.span = span;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public boolean isSmaple() {
        return isSmaple;
    }

    public void setSmaple(boolean isSmaple) {
        this.isSmaple = isSmaple;
    }

}
