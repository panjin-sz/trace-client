/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.meta.model;

import java.io.Serializable;
import java.util.Map;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceIndex.java 2016年7月21日 下午3:44:35 $
 */
public class TraceIndex implements Serializable {

    /**  */
    private static final long serialVersionUID = 321796164208757869L;

    /**
     * trace id
     */
    private String traceId;

    /**
     * partition id
     */
    private String partitionId;

    /**
     * method name
     */
    private String itemName;

    /**
     * start time of trace
     */
    private Long startTime;

    /**
     * the duration of invoke
     */
    private Integer duration;

    /**
     * exception
     */
    private Map<String, String> exception;

    /**
     * default constructor
     */
    public TraceIndex() {
    }

    /** getters and setters **/
    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(String partitionId) {
        this.partitionId = partitionId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Map<String, String> getException() {
        return exception;
    }

    public void setException(Map<String, String> exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "TraceIndex{" + "traceId='" + traceId + '\'' + ", partitionId='" + partitionId + '\'' + ", itemName='" + itemName + '\'' + ", startTime="
                + startTime + ", duration=" + duration + ", exception=" + exception + '}';
    }
}
