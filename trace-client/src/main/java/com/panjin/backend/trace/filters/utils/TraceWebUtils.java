/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters.utils;

import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.panjin.backend.trace.common.utils.NetUtils;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceWebUtils.java 2016年7月21日 下午5:49:40 $
 */
public abstract class TraceWebUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraceWebUtils.class.getName());

    private static final String IP = "ip";
    private static final String HOSTNAME = "hostname";
    private static LoadingCache<String, String> build = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {

        @Override
        public String load(String key) throws Exception {
            if (key.equals(IP)) {
                return NetUtils.getServerIp();// NetUtils.getIPAddress();
            } else if (key.equals(HOSTNAME)) {
                return NetUtils.getServerHostName();// NetUtils.getHostName();
            }

            throw new IllegalArgumentException("invalid key :" + key);
        }
    });

    /**
     * 获取IP
     *
     * @return
     */
    public static String getIPAddress() {
        String result;
        try {
            result = build.get(IP);
        } catch (ExecutionException e) {
            result = NetUtils.getServerIp();
            build.put(IP, result);
        }

        return result;
    }

    /**
     * 获取主机名
     *
     * @return
     */
    public static String getHostName() {
        String result;
        try {
            result = build.get(HOSTNAME);
        } catch (ExecutionException e) {
            result = NetUtils.getServerHostName();
            build.put(HOSTNAME, result);
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(getHostName());
        System.out.println(getIPAddress());

        System.out.println(getHostName());
        System.out.println(getIPAddress());
    }

    public void destroy() {
        build.cleanUp();
    }

    /**
     * 还原整个URL请求
     * 
     * <pre>
     *     example:  http://localhost:56231/test/action/pplibe?act=query
     *      String scheme = request.getScheme();            //请求类型，一般为http
     *      String serverName = request.getServerName();    //服务的ip地址，亦为服务的DNS服务名 localhost
     *      int serverPort = request.getServerPort();       //服务端口 56231
     *      String contextPath = request.getContextPath();  //应用名称 /test
     *      String servletPath = request.getServletPath();//进入该servlet定义的路径 /action/*
     *      String pathInfo = request.getPathInfo();    //自己定义的路径   /pplibe
     *      String queryString = request.getQueryString();//即为?后面的参数 例如act=query
     * 
     * </pre>
     * 
     * @param request
     * @return
     */
    public static String getURL(HttpServletRequest request) {

        StringBuilder urlBuilder = new StringBuilder(500);

        // String scheme = request.getScheme(); // 请求类型，一般为http
        // String serverName = request.getServerName(); // 服务的ip地址，亦为服务的DNS服务名
        // localhost
        // int serverPort = request.getServerPort(); // 服务端口 56231
        // urlBuilder.append(scheme).append(serverName).append(serverPort);

        String contextPath = request.getContextPath(); // 应用名称 /test
        String servletPath = request.getServletPath();// 进入该servlet定义的路径
                                                      // /action/*
        String pathInfo = request.getPathInfo(); // 自己定义的路径 /pplibe
        // String queryString = request.getQueryString();// 即为?后面的参数 例如act=query

        if (StringUtils.isNotBlank(contextPath)) {
            urlBuilder.append(contextPath);
        }

        if (StringUtils.isNotBlank(servletPath)) {
            urlBuilder.append(servletPath);
        }

        if (StringUtils.isNotBlank(pathInfo)) {
            urlBuilder.append(pathInfo);
        }

        // if (StringUtils.isNotBlank(queryString)) {
        // urlBuilder.append("?").append(queryString);
        // }
        return urlBuilder.toString();
    }

}
