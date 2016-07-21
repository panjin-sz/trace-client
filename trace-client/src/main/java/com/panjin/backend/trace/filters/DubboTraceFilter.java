/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.google.common.base.Preconditions;
import com.panjin.backend.trace.common.utils.EnvInfo;
import com.panjin.backend.trace.filters.enums.IdTypeEnums;
import com.panjin.backend.trace.filters.support.IdFactory;
import com.panjin.backend.trace.filters.support.Spans;
import com.panjin.backend.trace.filters.support.TraceClientConst;
import com.panjin.backend.trace.filters.utils.DubboUtils;
import com.panjin.backend.trace.filters.utils.TraceWebUtils;
import com.panjin.backend.trace.meta.model.Annotation;
import com.panjin.backend.trace.meta.model.Endpoint;
import com.panjin.backend.trace.meta.model.Span;
import com.panjin.backend.trace.meta.model.TraceType;

/**
 *
 *
 * @author panjin
 * @version $Id: DubboTraceFilter.java 2016年7月21日 下午6:11:09 $
 */
public class DubboTraceFilter implements Filter {

    static {
        InitOperation.init();
    }

    private static final Logger                        LOG         = LoggerFactory.getLogger(DubboTraceFilter.class);

    private Trace                                      trace       = Trace.getInstance();

    private final ConcurrentMap<String, AtomicInteger> concurrents = new ConcurrentHashMap<String, AtomicInteger>();

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        if (!trace.isOn()) {
            return invoker.invoke(invocation);
        }

        RpcContext context = RpcContext.getContext();
        boolean consumerSide = context.isConsumerSide();

        getConcurrent(invoker, invocation).incrementAndGet(); // 并发计数

        String spanName = DubboUtils.getSpanName(context);

        LOG.debug(" --- start filter appName {} spanName {} ", new String[] { trace.getAppName(), spanName });

        Span span = null;// 本次调用的span
        Endpoint endpoint = new Endpoint(TraceWebUtils.getIPAddress(), TraceWebUtils.getHostName(),
                                         context.getLocalPort());

        Result result = null;
        try {
            span = buildSpan(consumerSide, invocation, spanName);
            span.setHost(endpoint);

            if (!span.isSample()) {
                return invoker.invoke(invocation);
            }

            span.setAppName(trace.getAppName());

            startInvoke(span, consumerSide);

            RpcInvocation rpcInvocation = (RpcInvocation) invocation;
            setAttachment(span, rpcInvocation);
        } catch (Exception e) {
            LOG.warn("init  trace span error from service !" + EnvInfo.getEnvInfo(), e);
        }

        try {

            result = invoker.invoke(invocation);
            LOG.debug(" --- end filter appName {} spanName {} ", new String[] { trace.getAppName(), spanName });

            return result;
        } catch (Throwable e) {

            throw new RpcException(e);

        } finally {
            exceptionHandler(span, result);
            if (span != null) {
                // int contextSize = getRpcContextSize();
                endInvoke(consumerSide, span);

                // 记录并发数据
                int concurrent = getConcurrent(invoker, invocation).get(); // 当前并发数
                trace.logConcurrent(span, concurrent);

                // Log span
                trace.logSpan(span);
            }
            getConcurrent(invoker, invocation).decrementAndGet(); // 并发计数
        }

    }

    /**
     * 获取并发计数器
     */
    private AtomicInteger getConcurrent(Invoker<?> invoker, Invocation invocation) {
        String key = DubboUtils.getSpanName(invoker, invocation);
        AtomicInteger concurrent = concurrents.get(key);
        if (concurrent == null) {
            final AtomicInteger atomicInteger = new AtomicInteger();
            concurrent = concurrents.putIfAbsent(key, atomicInteger);
            if (concurrent == null) {
                concurrent = atomicInteger;
            }
        }
        return concurrent;
    }

    private Span buildSpan(boolean isConsumeSide, Invocation invocation, String spanName) {

        Span span = new Span();
        // 构造span
        if (isConsumeSide) { // 消费端

            Span parentSpan = null;
            boolean isWeb = trace.getWebContext() != null;
            boolean isService = trace.getServiceContext() != null;
            if (isWeb) {
                parentSpan = trace.getWebContext().getSpan();

            } else if (isService) {
                parentSpan = trace.getServiceContext().getSpan();
            }

            if (parentSpan == null) {
                span = trace.newSpan(spanName);
            } else {

                span = trace.genSpan(parentSpan.getTraceId(),
                                     IdFactory.getInstance().getNextId(IdTypeEnums.SPAN_ID.getType(), spanName),
                                     parentSpan.getId(), parentSpan.newSubRpcId(), spanName, parentSpan.isSample());
            }

            span.setAppType(trace.getType());
            span.setItemType(TraceType.CALL.getType());

        } else if (RpcContext.getContext().isProviderSide()) {
            String isSample = invocation.getAttachment(TraceClientConst.IS_SAMPLE);
            if (isSample == null) {
                // 外部程序直接调用Dubbo服务
                span = trace.newSpan(spanName);
            } else {
                // 被其他Dubbo服务调用
                String traceId = invocation.getAttachment(TraceClientConst.TRACE_ID);
                String parentId = invocation.getAttachment(TraceClientConst.PARENT_ID);
                String spanId = invocation.getAttachment(TraceClientConst.SPAN_ID);
                String rpcId = invocation.getAttachment(TraceClientConst.RPC_ID);
                span = trace.genSpan(traceId, spanId, parentId, rpcId, spanName, BooleanUtils.toBoolean(isSample));
            }

            // span.setAppType(AppType.SERVICE.getType());
            span.setAppType(trace.getType());
            span.setItemType(TraceType.SERVICE.getType());
        }
        return span;
    }

    private void exceptionHandler(Span span, Result result) {
        if (result != null && result.hasException()) {
            trace.logException(span, result.getException());
        }
    }

    /**
     * 设置下游各参数
     * 
     * @param span span
     * @param invocation invocation
     */
    private void setAttachment(Span span, RpcInvocation invocation) {

        Preconditions.checkNotNull(span, "span is null:" + span);
        // 调用其他Dubbo服务必须设置是否采样
        invocation.setAttachment(TraceClientConst.IS_SAMPLE, String.valueOf(span.isSample()));
        if (span.isSample()) {
            invocation.setAttachment(TraceClientConst.SPAN_ID, String.valueOf(span.getId()));
            invocation.setAttachment(TraceClientConst.PARENT_ID, String.valueOf(span.getParentId()));
            invocation.setAttachment(TraceClientConst.RPC_ID, String.valueOf(span.getRpcId()));
            invocation.setAttachment(TraceClientConst.TRACE_ID, String.valueOf(span.getTraceId()));
        }
    }

    /**
     * s 调用开始，生成CS|SR Annotation
     * 
     * @param span span
     * @param consumerSide 是否消费端
     */
    private void startInvoke(Span span, boolean consumerSide) {

        // int contextSize = getRpcContextSize();

        Long startTime = System.currentTimeMillis();
        if (consumerSide && span.isSample()) {

            // CS Annotation
            Annotation annotation = Spans.genAnnotation(Annotation.AnnType.CS, startTime);
            span.addAnnotation(annotation);
        } else {

            TraceContext traceContext = new TraceContext();

            if (span.isSample()) {

                // SR Annotation
                Annotation annotation = Spans.genAnnotation(Annotation.AnnType.SR, startTime);
                span.addAnnotation(annotation);
            }
            traceContext.setSpan(span);

            trace.setServiceContext(traceContext);
        }
    }

    /**
     * 调用结束，生成CR|SS Annotation
     * 
     * @param consumerSide 是否消费端
     * @param span span
     */
    private void endInvoke(boolean consumerSide, Span span) {

        long endTime = System.currentTimeMillis();
        // int contextSize = getRpcContextSize();

        if (consumerSide && span.isSample()) {

            // CR Annotation
            Annotation annotation = Spans.genAnnotation(Annotation.AnnType.CR, endTime);
            span.addAnnotation(annotation);
        } else {

            if (span.isSample()) {

                // SS Annotation
                Annotation annotation = Spans.genAnnotation(Annotation.AnnType.SS, endTime);
                span.addAnnotation(annotation);
            }

            trace.removeServiceContext();
            // tracer.removeParentSpan();
        }
    }

    /**
     * 获取RPC调用上下文大小
     * 
     * @return
     */
    private int getRpcContextSize() {
        int size = 0;
        RpcContext context = RpcContext.getContext();
        // attachments
        Map<String, String> attachmentsMap = context.getAttachments();
        if (attachmentsMap != null) {
            for (Map.Entry<String, String> entry : attachmentsMap.entrySet()) {
                size += stringLength(entry.getKey());
                size += stringLength(entry.getValue());
            }
        }
        return size;
    }

    private int stringLength(String str) {
        return (str == null) ? 0 : str.length();
    }

}
