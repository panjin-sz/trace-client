/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panjin.backend.trace.common.utils.EnvInfo;
import com.panjin.backend.trace.filters.support.Spans;
import com.panjin.backend.trace.filters.support.TraceClientConst;
import com.panjin.backend.trace.filters.utils.TraceWebUtils;
import com.panjin.backend.trace.meta.model.Annotation;
import com.panjin.backend.trace.meta.model.Span;
import com.panjin.backend.trace.meta.model.TraceType;

/**
 *
 *
 * @author panjin
 * @version $Id: WebTraceFilter.java 2016年7月21日 下午6:18:17 $
 */
public class WebTraceFilter implements Filter {

    private static final Logger                        LOG         = LoggerFactory.getLogger(WebTraceFilter.class);

    private Trace                                      trace       = Trace.getInstance();

    private final ConcurrentMap<String, AtomicInteger> concurrentMap = new ConcurrentHashMap<String, AtomicInteger>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        LOG.info("init by WebTraceFilter");
        InitOperation.init();

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                             ServletException {


        // 判断是否开启了trace
        if (!trace.isOn()) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        TraceContext traceContext = new TraceContext();
        trace.setWebContext(traceContext);

        try {

            // 判断是否忽略trace此uri
            if (httpRequest.getRequestURI().contains("WEB-INF")) {
                chain.doFilter(request, response);
                return;
            }

            // 是否采样数据
            boolean sample = trace.getSampler().isSample() && !trace.isInBlacklist(httpRequest.getRequestURI());
            traceContext.setSmaple(sample);
            if (!sample) {
                chain.doFilter(request, response);
                return;
            }

            // 1.之前操作
            doBefore(httpRequest, httpResponse, traceContext, chain);

        } catch (Exception e) {
            LOG.warn("init trace span error from web " + EnvInfo.getEnvInfo(), e);
        }

        // CountResponseWrapper countResponseWrapper = new CountResponseWrapper(httpResponse);

        try {

            chain.doFilter(request, response);

        } catch (Throwable e) {

            // 记录日志
            Trace.getInstance().logException(null, e);

            // 继续抛出异常
            throw new ServletException(e);

        } finally {

            // 2.之后操作
            doAfter(httpRequest, httpResponse, traceContext, chain);

        }

    }

    /**
     * 过滤器之前的操作
     * 
     * @param traceContext
     * @param chain
     */
    private void doBefore(HttpServletRequest request, HttpServletResponse response, TraceContext traceContext,
                          FilterChain chain) {

        // 并发计数
        getConcurrent(request).incrementAndGet();

        String spanName = TraceWebUtils.getURL(request);
        String traceId = request.getHeader(TraceClientConst.X_TRACE_ID);

        Span span;
        if (StringUtils.isBlank(traceId)) { // root
            traceContext.setRoot(true);

            span = trace.newSpan(spanName);
            span.setItemType(TraceType.URL.getType());
            span.setAppType(trace.getType());
            span.setAppName(trace.getAppName());
            span.setSample(traceContext.isSmaple());

        } else { // child
            String parentId = request.getHeader(TraceClientConst.X_TRACE_PARENT_ID);
            String spanId = request.getHeader(TraceClientConst.X_TRACE_SPAN_ID);
            String rpcId = request.getHeader(TraceClientConst.X_TRACE_RPC_ID);

            span = trace.genSpan(traceId, spanId, parentId, rpcId, spanName, true);
        }

        // int contentLength = request.getContentLength();
        // if (contentLength < TraceConst.EMPTY_SIZE) {
        // contentLength = TraceConst.EMPTY_SIZE;
        // }
        span.setHost(trace.loadEndPort());

        Annotation annotation = Spans.genAnnotation(Annotation.AnnType.SR, System.currentTimeMillis());
        span.addAnnotation(annotation);

        traceContext.setSpan(span);

        // response 中增加 trace 相关的 Headers
        response.addHeader(TraceClientConst.X_TRACE_ID, span.getTraceId());
        response.addHeader(TraceClientConst.X_TRACE_SPAN_ID, span.getId());
        response.addHeader(TraceClientConst.X_TRACE_PARENT_ID, span.getParentId());
        response.addHeader(TraceClientConst.X_TRACE_RPC_ID, span.getRpcId());
    }

    /**
     * 过滤器之后的操作
     * 
     * @param traceContext
     * @param chain
     */
    private void doAfter(HttpServletRequest request, HttpServletResponse response, TraceContext traceContext,
                         FilterChain chain) {

        try {

            if (traceContext.getSpan() == null || !traceContext.getSpan().isSample()) {
                return;
            }

            Span span = traceContext.getSpan();

            Annotation annotation = Spans.genAnnotation(Annotation.AnnType.SS, System.currentTimeMillis());
            span.addAnnotation(annotation);

            int concurrent = getConcurrent(request).get();
            trace.logConcurrent(span, concurrent);

            trace.logSpan(span);

        } finally {

            // 清除上下文信息
            Trace.getInstance().removeWebContext();
        }

        // 并发计数
        getConcurrent(request).decrementAndGet();

    }

    @Override
    public void destroy() {

    }

    /**
     * 获取并发计数器
     */
    private AtomicInteger getConcurrent(HttpServletRequest request) {
        String key = TraceWebUtils.getURL(request);
        AtomicInteger concurrent = concurrentMap.get(key);
        if (concurrent == null) {
            final AtomicInteger ai = new AtomicInteger();
            concurrent = concurrentMap.putIfAbsent(key, ai);
            if (concurrent == null) {
                concurrent = ai;
            }
        }
        return concurrent;
    }

    private static final class CountResponseWrapper extends HttpServletResponseWrapper {

        private CountServletOutputStream countServletOutputStream;
        private PrintWriter              printWriter;

        public CountResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (countServletOutputStream == null) {
                countServletOutputStream = new CountServletOutputStream(super.getOutputStream());
            }
            return countServletOutputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (printWriter == null) {
                printWriter = new PrintWriter(new OutputStreamWriter(this.getOutputStream(), Charset.forName("UTF-8")));
            }
            return printWriter;
        }

        public int getWrittenSize() {
            if (countServletOutputStream == null) {
                return 0;
            }
            return countServletOutputStream.getWrittenSize();
        }
    }

    private static final class CountServletOutputStream extends ServletOutputStream {

        private ServletOutputStream sos;
        private int                 writtenSize = 0;

        private CountServletOutputStream(ServletOutputStream sos) {
            super();
            this.sos = sos;
        }

        @Override
        public void flush() throws IOException {
            sos.flush();
        }

        @Override
        public void close() throws IOException {
            sos.close();
        }

        @Override
        public void write(int b) throws IOException {
            sos.write(b);
            writtenSize += 1;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            sos.write(b, off, len);
            writtenSize += len;
        }

        @Override
        public void write(byte[] b) throws IOException {
            sos.write(b);
            writtenSize += b.length;
        }

        public int getWrittenSize() {
            return writtenSize;
        }
    }
}
