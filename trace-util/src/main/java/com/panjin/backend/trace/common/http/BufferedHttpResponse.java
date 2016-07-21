/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.http;

import java.util.Map;

import org.apache.http.StatusLine;

/**
 *
 *
 * @author panjin
 * @version $Id: BufferedHttpResponse.java 2016年7月21日 下午5:21:46 $
 */
public class BufferedHttpResponse {
    private StatusLine          statusLine;
    private String              content;
    private Map<String, String> headers;

    public BufferedHttpResponse(StatusLine statusLine, String content, Map<String, String> headers) {
        this.statusLine = statusLine;
        this.content = content;
        this.headers = headers;
    }

    /**
     * 获取HTTP响应状态
     *
     * @return
     */
    public StatusLine getStatusLine() {
        return statusLine;
    }

    /**
     * 获取HTTP响应内容
     *
     * @return
     */
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return statusLine.getStatusCode() + " " + content;
    }

    public int getStatusCode() {
        return statusLine.getStatusCode();
    }

    public boolean isOK() {
        return statusLine.getStatusCode() == 200 ? true : false;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

}
