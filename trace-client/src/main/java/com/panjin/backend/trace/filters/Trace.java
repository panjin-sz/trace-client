/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.json.JSON;
import com.google.common.base.Preconditions;
import com.panjin.backend.trace.common.utils.EnvInfo;
import com.panjin.backend.trace.common.utils.NetUtils;
import com.panjin.backend.trace.filters.enums.IdTypeEnums;
import com.panjin.backend.trace.filters.sampler.DefaultSampler;
import com.panjin.backend.trace.filters.sampler.Sampler;
import com.panjin.backend.trace.filters.support.IdFactory;
import com.panjin.backend.trace.filters.support.TraceClientConst;
import com.panjin.backend.trace.filters.utils.TraceErrorUtils;
import com.panjin.backend.trace.filters.utils.TraceWebUtils;
import com.panjin.backend.trace.meta.model.AppType;
import com.panjin.backend.trace.meta.model.BinaryAnnotation;
import com.panjin.backend.trace.meta.model.Endpoint;
import com.panjin.backend.trace.meta.model.Span;
import com.panjin.backend.trace.meta.support.TraceConstants;

/**
 *
 *
 * @author panjin
 * @version $Id: Trace.java 2016年7月21日 下午5:41:17 $
 */
public class Trace {

    private static final Logger LOG = LoggerFactory.getLogger(Trace.class);

    /**
     * 单独记录trace日志
     */
    private static final Logger TRACE_LOG = LoggerFactory.getLogger("traceLogger");

    private static final String DOMAIN = "trace.client.domain"; // 应用域
    private static final String APP_NAME = "trace.client.appname"; // 应用名称
    private static final String TYPE = "trace.client.type"; // 应用类型
    private static final String SWITCH = "trace.client.switch"; // 是否开启trace
    private static final String SAMPLE_RATE = "trace.client.sample.rate"; // 采样率
    private static final String BLACKLIST_REGEX = "trace.client.blacklist.regex"; // 匹配不做trace的url
    private static final Trace instance = new Trace();
    // 针对web的上下文
    private static ThreadLocal<TraceContext> webContext = new ThreadLocal<TraceContext>();
    // 针对service的上下文，只能满足顺序调用的场景。
    private static ThreadLocal<TraceContext> serviceContext = new ThreadLocal<TraceContext>();
    // 针对DAO的上下文
    private static ThreadLocal<TraceContext> daoContext = new ThreadLocal<TraceContext>();
    private volatile boolean isInit = false;
    // 采样算法
    private Sampler sampler;
    private Pattern blacklistPattern;
    private TraceConfig traceConfig;

    private Trace() {

    }

    public static Trace getInstance() {
        return instance;
    }

    /**
     * 初始化trace的一些基本配置信息
     * 
     * @param properties
     */
    public void init(Properties properties) {

        Preconditions.checkNotNull(properties, "properties is null!");

        // if (!isInit) {
        synchronized (this) {
            // if (!isInit) {

            try {
                // 定义采样器
                String sampleRate = properties.getProperty(SAMPLE_RATE, DefaultSampler.DEFAULT_SAMPLE_RATE);
                sampleRate = NumberUtils.isNumber(sampleRate) ? sampleRate : DefaultSampler.DEFAULT_SAMPLE_RATE;
                sampler = new DefaultSampler(Float.valueOf(sampleRate));

                // 获取应用信息
                String hostName = TraceWebUtils.getHostName();
                String ipAddress = TraceWebUtils.getIPAddress();

                String appName = properties.getProperty(DOMAIN, hostName) + "#" + properties.getProperty(APP_NAME, hostName);// 若是应用名词为空的话，那么取主机名字

                String property = properties.getProperty(SWITCH, "false");// 默认不开启trace
                boolean switchValue = Boolean.valueOf(property);
                String type = properties.getProperty(TYPE, AppType.WEB.getType());

                traceConfig = new TraceConfig(appName, type, switchValue, Float.valueOf(sampleRate), ipAddress, hostName);

                // url过滤正则表达式
                String regex = properties.getProperty(BLACKLIST_REGEX, "");
                if (!StringUtils.isEmpty(regex)) {
                    blacklistPattern = Pattern.compile(regex);
                }

                // 初始化ID工厂
                IdFactory.getInstance().init(appName, NetUtils.getServerIp());

                isInit = true;

                LOG.info("init trace config success .hostName:{} , ipAddress:{}, appName:{} , switch：{} , sampleRate:{} type:{}", new Object[] { hostName,
                        ipAddress, appName, switchValue, String.valueOf(sampleRate), type });
            } catch (Exception e) {

                isInit = false;
                LOG.error("init trace config failed." + EnvInfo.getEnvInfo(), e);
            }
        }
        // }
        // }

    }

    /**
     * 是否开启了trace
     * 
     * @return
     */
    public boolean isOn() {

        Preconditions.checkNotNull(traceConfig, "config is null!");

        return isInit && traceConfig.isSwitchValue();
    }

    /**
     * 是否在trace黑名单中
     * 
     * @return
     */
    public boolean isInBlacklist(String item) {

        Preconditions.checkNotNull(item, "item is null!");

        if (blacklistPattern == null) {
            return false;
        }
        return blacklistPattern.matcher(item).matches();
    }

    /**
     * 获取应用名称
     * 
     * @return
     */
    public String getAppName() {

        Preconditions.checkNotNull(traceConfig, "config is null!");
        return traceConfig.getAppName();
    }

    public String getType() {

        Preconditions.checkNotNull(traceConfig, "config is null!");
        return traceConfig.getType();
    }

    public TraceConfig getTraceConfig() {
        return traceConfig;
    }

    public void destroy() {

        removeWebContext();
        removeServiceContext();
        removeDaoContext();

        LOG.info("remove trace success!");
    }

    public void setWebContext(TraceContext traceContext) {
        webContext.set(traceContext);
    }

    public TraceContext getWebContext() {
        return webContext.get();
    }

    public void removeWebContext() {
        webContext.remove();
    }

    public TraceContext getServiceContext() {
        return serviceContext.get();
    }

    public void setServiceContext(TraceContext traceContext) {
        serviceContext.set(traceContext);
    }

    public void removeServiceContext() {
        serviceContext.remove();
    }

    public TraceContext getDaoContext() {
        return daoContext.get();
    }

    public void setDaoContext(TraceContext traceContext) {
        daoContext.set(traceContext);
    }

    public void removeDaoContext() {
        daoContext.remove();
    }

    /**
     * 新建span
     * 
     * @param spanName
     * @return
     */
    public Span newSpan(String spanName) {
        Span span = new Span();
        boolean sample = sampler.isSample();
        span.setSpanName(spanName);
        span.setSample(sample);
        span.setRpcId(TraceClientConst.INIT_RPC_ID);
        if (sample) {
            span.setTraceId(IdFactory.getInstance().getNextId(IdTypeEnums.TRACE_ID.getType(), spanName));
            span.setId(IdFactory.getInstance().getNextId(IdTypeEnums.SPAN_ID.getType(), spanName));
        }

        return span;
    }

    /**
     * 生成子span
     * 
     * @param traceId
     * @param parentId
     * @param spanName
     * @param isSample
     * @return
     */
    public Span genSpan(String traceId, String spanId, String parentId, String rpcId, String spanName, boolean isSample) {

        Span span = new Span();
        span.setTraceId(traceId);
        span.setRpcId(rpcId);
        span.setId(spanId);
        span.setParentId(parentId);
        span.setSpanName(spanName);
        span.setSample(isSample);
        return span;
    }

    public Endpoint loadEndPort() {
        return new Endpoint(getTraceConfig().getIpAddress(), getTraceConfig().getHostName(), TraceClientConst.DEFAULT_PORT);
    }

    public boolean isSample() {
        Preconditions.checkNotNull(sampler, "sample is null ");

        return sampler.isSample();
    }

    /**
     * 记录异常Annotation
     * 
     * @param exception
     *            异常
     */
    public void logException(Span span, Throwable exception) {
        String exceptionInfo = exception == null ? "exception is null " : TraceErrorUtils.configStackTrace(exception);
        addBinaryAnnotation(span, TraceConstants.EXCEPTION_KEY, exceptionInfo, TraceConstants.EXCEPTION_TYPE);
        String exceptionName = (exception == null ? "null" : exception.getClass().getName());
        addBinaryAnnotation(span, TraceConstants.EXCEPTION_NAME_KEY, exceptionName, TraceConstants.EXCEPTION_TYPE);
    }

    /**
     * 记录并发数Annotation
     * 
     * @param span
     * @param concurrent
     */
    public void logConcurrent(Span span, int concurrent) {
        addBinaryAnnotation(span, TraceConstants.CONCURRENT_KEY, String.valueOf(concurrent), TraceConstants.INFO_TYPE);
    }

    private void addBinaryAnnotation(Span span, String key, String value, String type) {
        BinaryAnnotation annotation = new BinaryAnnotation();
        annotation.setKey(key);
        annotation.setValue(value);
        annotation.setType(type);

        if (span == null) {
            if (getServiceContext() != null) {
                span = getServiceContext().getSpan();
            } else if (getWebContext() != null) {
                span = getWebContext().getSpan();
            }
        }

        if (span != null) {
            span.addBinaryAnnotation(annotation);
            return;
        }

        LOG.warn("get span from context error. " + EnvInfo.getEnvInfo());
    }

    public void logSpan(Span span) {

        if (!span.isSample()) {
            return;
        }

        try {
            TRACE_LOG.info(JSON.json(span));
        } catch (IOException e) {

            LOG.error("span to json error. " + EnvInfo.getEnvInfo(), e);
        }
    }

    public Sampler getSampler() {
        return sampler;
    }
}
