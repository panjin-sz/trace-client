/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

/**
 *
 *
 * @author panjin
 * @version $Id: HttpHandler.java 2016年7月21日 下午5:22:38 $
 */
public class HttpHandler {

    private static final String CHARSET = "UTF-8";
    private String              url     = null;
    private String              content = null;
    private int                 connectTimeout;
    private int                 socketTimeout;
    private List<Header>        headers = new ArrayList<Header>();

    public HttpHandler(int connectTimeout, int socketTimeout) {
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
    }

    public HttpHandler() {
        this.connectTimeout = 5000;
        this.socketTimeout = 5000;
    }

    /**
     * 添加Header
     *
     * @param name
     * @param value
     * @return
     */
    public HttpHandler addHeader(String name, String value) {
        headers.add(new BasicHeader(name, value));
        return this;
    }

    /**
     * 设置请求URL
     *
     * @param url
     * @return
     */
    public HttpHandler setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * 设置请求Body
     *
     * @param content
     * @return
     */
    public HttpHandler setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 请求转字符串
     *
     * @return
     */
    @Override
    public String toString() {
        String str = "\n[url]";
        if (url != null) {
            str += url;
        }
        str += "\n[headers]";
        if (headers.size() > 0) {
            str += Arrays.toString(headers.toArray());
        }
        str += "\n[content]";
        if (content != null) {
            str += content;
        }
        return str;
    }

    /**
     * 调用HTTP GET请求
     *
     * @return
     */
    public BufferedHttpResponse get() throws IOException {
        HttpGet get = new HttpGet(url);
        setHeaders(get);
        return execute(get);
    }

    /**
     * 调用HTTP HEAD请求
     *
     * @return
     */
    public BufferedHttpResponse head() throws IOException {
        HttpHead head = new HttpHead(url);
        setHeaders(head);
        return execute(head);
    }

    /**
     * 调用HTTP DELETE请求
     *
     * @return
     */
    public BufferedHttpResponse delete() throws IOException {
        HttpDelete delete = new HttpDelete(url);
        setHeaders(delete);
        return execute(delete);
    }

    /**
     * 调用HTTP PUT请求
     *
     * @return
     */
    public BufferedHttpResponse put() throws IOException {
        HttpPut put = new HttpPut(url);
        setHeaders(put);
        setContent(put);
        return execute(put);
    }

    /**
     * 调用HTTP POST请求
     *
     * @return
     */
    public BufferedHttpResponse post() throws IOException {
        HttpPost post = new HttpPost(url);
        setHeaders(post);
        setContent(post);
        return execute(post);
    }

    private HttpRequestBase setHeaders(HttpRequestBase request) {
        for (Header h : headers) {
            request.addHeader(h);
        }
        return request;
    }

    private HttpEntityEnclosingRequestBase setContent(HttpEntityEnclosingRequestBase request) throws IOException {
        if (content != null) {
            request.setEntity(new StringEntity(content, CHARSET));
        }
        return request;
    }

    private BufferedHttpResponse execute(HttpRequestBase request) throws IOException {
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        configBuilder.setSocketTimeout(socketTimeout);
        configBuilder.setConnectTimeout(connectTimeout);
        RequestConfig requestConfig = configBuilder.build();
        request.setConfig(requestConfig);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(request);
            Map<String, String> headerMap = new HashMap<String, String>();
            Header[] headerArray = httpResponse.getAllHeaders();
            for (Header h : headerArray) {
                headerMap.put(h.getName(), h.getValue());
            }
            StatusLine statusLine = httpResponse.getStatusLine();
            HttpEntity entity = httpResponse.getEntity();
            String responseContent = (entity == null) ? "" : EntityUtils.toString(entity, CHARSET);
            EntityUtils.consume(entity);

            return new BufferedHttpResponse(statusLine, responseContent, headerMap);
        } finally {
            httpClient.close();
        }
    }
}
