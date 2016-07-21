/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.model;

import java.io.Serializable;

/**
 *
 *
 * @author panjin
 * @version $Id: Endpoint.java 2016年7月21日 下午3:38:44 $
 */
public class Endpoint implements Serializable {

    /**  */
    private static final long serialVersionUID = -4057809699278961627L;

    private String ip;
    private String host;
    private Integer port;

    public Endpoint() {
    }

    public Endpoint(String ip, String host, Integer port) {
        this.ip = ip;
        this.host = host;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Endpoint{" + "ip='" + ip + '\'' + ", host='" + host + '\'' + ", port=" + port + '}';
    }
}
