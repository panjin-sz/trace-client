/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters.support;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceClientConst.java 2016年7月21日 下午5:56:35 $
 */
public class TraceClientConst {

    public static final String X_TRACE_ROOT_ID   = "X-TRACE-ROOT-ID";
    public static final String X_TRACE_PARENT_ID = "X-TRACE-PARENT-ID";
    public static final String X_TRACE_RPC_ID    = "X-TRACE-RPC-ID";
    public static final String X_TRACE_ID        = "X-TRACE-ID";
    public static final String X_TRACE_SPAN_ID   = "X-TRACE-SPAN-ID";

    public static final int    DEFAULT_PORT      = 0;                  // 服务的默认端口

    public static final String TRACE_ID          = "trace_id";
    public static final String PARENT_ID         = "parent_id";
    public static final String RPC_ID            = "rpc_id";
    public static final String SPAN_ID           = "span_id";
    public static final String IS_SAMPLE         = "is_sample";

    public static final String INIT_RPC_ID       = "0";

    public static final int    EMPTY_SIZE        = 0;

    public static final String NULL_STRING       = "NULL";
}
