/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.model;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceType.java 2016年7月21日 下午3:46:44 $
 */
public enum TraceType {
    
    URL("url", "web请求"),

    CALL("call", "服务调用"),

    SERVICE("service", "服务端提供"),

    SQL("sql", "数据库操作"), ;

    private String type;

    private String desc;

    TraceType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
