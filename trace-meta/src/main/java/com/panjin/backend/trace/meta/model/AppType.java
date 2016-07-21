/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.meta.model;

/**
 *
 *
 * @author panjin
 * @version $Id: AppType.java 2016年7月21日 下午5:53:20 $
 */
public enum AppType {
    WEB("web", "web节点"),

    SERVICE("service", "服务端节点"), ;

    private String type;

    private String desc;

    AppType(String type, String desc) {
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
