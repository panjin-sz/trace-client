/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.meta.model;

import java.io.Serializable;

/**
 *
 *
 * @author panjin
 * @version $Id: Annotation.java 2016年7月21日 下午3:38:03 $
 */
public class Annotation implements Serializable {

    /**  */
    private static final long serialVersionUID = -4716344391966249997L;

    private Long timestamp;
    private Integer size;
    private Endpoint host;
    private AnnType value;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Endpoint getHost() {
        return host;
    }

    public void setHost(Endpoint host) {
        this.host = host;
    }

    public AnnType getValue() {
        return value;
    }

    public void setValue(AnnType value) {
        this.value = value;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Annotation{" + "timestamp=" + timestamp + ", size=" + size + ", host=" + host + ", value=" + value + '}';
    }

    public enum AnnType {
        CS, CR, SS, SR
    }
}
