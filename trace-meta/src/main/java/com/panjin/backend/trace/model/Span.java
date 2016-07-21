/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author panjin
 * @version $Id: Span.java 2016年7月21日 下午3:42:37 $
 */
public class Span implements Serializable {

    /**  */
    private static final long serialVersionUID = -6173103558900748225L;

    public static final char RPC_ID_SEPARATOR = '.';

    private String traceId;
    private String id;
    private String parentId;
    private String rpcId;
    private String spanName;

    private String appName; // 应用名称

    private String appType;
    private String itemType;

    private transient int rpcCounter;

    private boolean sample; // 应用类型

    private Endpoint host;

    private List<Annotation> annotations = new ArrayList<Annotation>();
    private List<BinaryAnnotation> binaryAnnotations = new ArrayList<BinaryAnnotation>();

    public synchronized String newSubRpcId() {
        ++rpcCounter;
        return rpcId + RPC_ID_SEPARATOR + rpcCounter;
    }

    public String getRpcId() {
        return rpcId;
    }

    public void setRpcId(String rpcId) {
        this.rpcId = rpcId;
    }

    public Endpoint getHost() {
        return host;
    }

    public void setHost(Endpoint host) {
        this.host = host;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public boolean isSample() {
        return sample;
    }

    public void setSample(boolean sample) {
        this.sample = sample;
    }

    public String getSpanName() {
        return spanName;
    }

    public void setSpanName(String spanName) {
        this.spanName = spanName;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public List<BinaryAnnotation> getBinaryAnnotations() {
        return binaryAnnotations;
    }

    public void setBinaryAnnotations(List<BinaryAnnotation> binaryAnnotations) {
        this.binaryAnnotations = binaryAnnotations;
    }

    public void addBinaryAnnotation(BinaryAnnotation binaryAnnotation) {
        binaryAnnotations.add(binaryAnnotation);
    }

    public void addAnnotation(Annotation annotation) {
        annotations.add(annotation);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    @Override
    public String toString() {
        return "Span{" + "traceId='" + traceId + '\'' + ", id='" + id + '\'' + ", parentId='" + parentId + '\'' + ", rpcId='" + rpcId + '\'' + ", spanName='"
                + spanName + '\'' + ", appName='" + appName + '\'' + ", appType='" + appType + '\'' + ", itemType='" + itemType + '\'' + ", sample=" + sample
                + ", annotations=" + annotations + ", binaryAnnotations=" + binaryAnnotations + '}';
    }
}
