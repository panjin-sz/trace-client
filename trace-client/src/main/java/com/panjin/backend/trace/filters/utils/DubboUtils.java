/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters.utils;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcContext;
import com.google.common.base.Preconditions;

/**
 *
 *
 * @author panjin
 * @version $Id: DubboUtils.java 2016年7月21日 下午6:01:20 $
 */
public class DubboUtils {

    /**
     * 生成spanName by RpcContext
     * 
     * @param context
     * @return
     */
    public static String getSpanName(RpcContext context) {
        Preconditions.checkNotNull(context);

        String serviceName = context.getUrl().getServiceInterface(); // serviceName
        String methodName = context.getMethodName(); // 方法名
        String generic = context.getUrl().getParameter("generic");  // 处理泛化调用
        if ("true".equals(generic)) {
            Object[] rpcArguments = context.getArguments();
            if (rpcArguments != null && rpcArguments.length > 0) {
                methodName = String.valueOf(rpcArguments[0]);
            }
        }
        return serviceName + "." + methodName;
    }

    /**
     * 生成spanName by Invoker and Invocation
     *
     * @param invoker
     * @param invocation
     * @return
     */
    public static String getSpanName(Invoker<?> invoker, Invocation invocation) {

        Preconditions.checkNotNull(invoker);
        Preconditions.checkNotNull(invocation);

        return invoker.getInterface().getName() + "." + invocation.getMethodName();
    }
}
