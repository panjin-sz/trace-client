/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters.support;

import com.panjin.backend.trace.filters.Trace;
import com.panjin.backend.trace.filters.enums.IdTypeEnums;
import com.panjin.backend.trace.meta.model.Annotation;
import com.panjin.backend.trace.meta.model.Span;
import com.panjin.backend.trace.meta.model.TraceType;

/**
 *
 *
 * @author panjin
 * @version $Id: ClassFileTransformCode.java 2016年7月21日 下午5:40:21 $
 */
public abstract class ClassFileTransformCode {
    
    private void beforeCode() throws Throwable {
        String spanName = ""; // set span name
        Trace trace = Trace.getInstance();
        // $Type result;
        if (trace.isOn()) {
            Span parentSpan = null;
            boolean isWeb = trace.getWebContext() != null;
            boolean isService = trace.getServiceContext() != null;
            if (isWeb) {
                parentSpan = trace.getWebContext().getSpan();
            } else if (isService) {
                parentSpan = trace.getServiceContext().getSpan();
            }

            Span span;
            if (parentSpan == null) {
                span = trace.newSpan(spanName);
            } else {
                span = trace.genSpan(parentSpan.getTraceId(),
                                     IdFactory.getInstance().getNextId(IdTypeEnums.SPAN_ID.getType(), spanName),
                                     parentSpan.getId(), parentSpan.newSubRpcId(), spanName, parentSpan.isSample());
            }

            // String appType = isWeb ? AppType.WEB.getType() : AppType.SERVICE.getType();
            span.setAppType(trace.getType());
            span.setItemType(TraceType.SQL.getType());
            span.setAppName(trace.getAppName());
            span.setHost(trace.loadEndPort());
            Annotation startAnno = Spans.genAnnotation(Annotation.AnnType.CS, System.currentTimeMillis());
            span.addAnnotation(startAnno);

            try {
                // result =
                // invoke original method
            } catch (Throwable t) {
                trace.logException(span, t);
                trace.logSpan(span);
                throw t;
            }

            Annotation endAnno = Spans.genAnnotation(Annotation.AnnType.CR, System.currentTimeMillis());
            span.addAnnotation(endAnno);

            trace.logSpan(span);
        } else {
            // result =
            // invoke original method
        }
        // return result;
    }
}
