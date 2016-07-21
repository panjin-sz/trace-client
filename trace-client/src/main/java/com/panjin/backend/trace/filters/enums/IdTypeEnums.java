/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters.enums;

/**
 *
 *
 * @author panjin
 * @version $Id: IdTypeEnums.java 2016年7月21日 下午5:32:23 $
 */
public enum IdTypeEnums {

    SPAN_ID(1, "生成SPAN的ID"),

    TRACE_ID(2, "生成TRACE的ID");

    private int type;

    private String desc;

    IdTypeEnums(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
