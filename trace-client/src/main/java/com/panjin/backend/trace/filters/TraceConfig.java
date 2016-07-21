/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters;

/**
 * trace 配置类
 *
 * @author panjin
 * @version $Id: TraceConfig.java 2016年7月21日 下午5:48:34 $
 */
public class TraceConfig {
    // 应用名称
    private String  appName;

    // 应用类型
    private String  type;

    // 是否开启
    private boolean switchValue;

    // 采样率
    private float   rate;

    private String  ipAddress;

    private String  hostName;

    public TraceConfig(String appName, String type, boolean switchValue, float rate, String ipAddress, String hostName) {
        this.appName = appName;
        this.type = type;
        this.switchValue = switchValue;
        this.rate = rate;
        this.ipAddress = ipAddress;
        this.hostName = hostName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isSwitchValue() {
        return switchValue;
    }

    public void setSwitchValue(boolean switchValue) {
        this.switchValue = switchValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

}
