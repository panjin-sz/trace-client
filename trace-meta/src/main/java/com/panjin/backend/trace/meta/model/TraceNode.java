/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.meta.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceNode.java 2016年7月21日 下午3:45:41 $
 */
public class TraceNode {
    private String id;
    private String parentId;
    private String rpcId;
    private String appName;
    private String itemName;
    private Integer duration;
    private Endpoint endpoint;
    private Integer size;
    private String type;
    private boolean leaf;
    private Long timestamp;
    private Map<String, String> exception = new HashMap<String, String>();
    private List<TraceNode> children = new ArrayList<TraceNode>();
    private List<TraceNode> brother = new ArrayList<TraceNode>();

    public String getRpcId() {
        return rpcId;
    }

    public void setRpcId(String rpcId) {
        this.rpcId = rpcId;
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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TraceNode> getChildren() {
        return children;
    }

    public void setChildren(List<TraceNode> children) {
        this.children = children;
    }

    public Map<String, String> getException() {
        return exception;
    }

    public void setException(Map<String, String> exception) {
        this.exception = exception;
    }

    public void addException(String key, String value) {
        this.exception.put(key, value);
    }

    public void addChild(TraceNode traceNode) {
        children.add(traceNode);
    }

    public void setBrother(List<TraceNode> brother) {
        this.brother = brother;
    }

    public List<TraceNode> getBrother() {
        return this.brother;
    }

    public void addBrother(TraceNode traceNode) {
        brother.add(traceNode);
    }
}
