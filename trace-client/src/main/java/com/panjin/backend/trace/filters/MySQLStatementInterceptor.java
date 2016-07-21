/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysql.jdbc.*;
import com.panjin.backend.trace.common.utils.SqlUtils;
import com.panjin.backend.trace.filters.enums.IdTypeEnums;
import com.panjin.backend.trace.filters.support.IdFactory;
import com.panjin.backend.trace.filters.support.Spans;
import com.panjin.backend.trace.meta.model.Annotation;
import com.panjin.backend.trace.meta.model.Span;
import com.panjin.backend.trace.meta.model.TraceType;

/**
 *
 *
 * @author panjin
 * @version $Id: MySQLStatementInterceptor.java 2016年7月21日 下午6:14:22 $
 */
public class MySQLStatementInterceptor implements StatementInterceptorV2 {

    private static final Logger LOG   = LoggerFactory.getLogger(MySQLStatementInterceptor.class);

    private Trace               trace = Trace.getInstance();

    @Override
    public void init(Connection connection, Properties properties) throws SQLException {
    }

    @Override
    public ResultSetInternalMethods preProcess(String sql, Statement statement, Connection connection)
                                                                                                      throws SQLException {

        if (!trace.isOn()) {
            return null;
        }

        try {
            final String sqlToLog;
            if (statement instanceof PreparedStatement) {
                sqlToLog = ((PreparedStatement) statement).getPreparedSql();
            } else {
                sqlToLog = sql;
            }
            // 某些sql不必跟踪
            if (StringUtils.isBlank(sqlToLog)) {
                // LOG.info("Ignore sql {}", sqlToLog);
                return null;
            }
            final String upperCaseSql = sqlToLog.trim().toUpperCase(Locale.getDefault());
            if (upperCaseSql.startsWith("/*") || upperCaseSql.startsWith("SET") || upperCaseSql.startsWith("SHOW")
                || upperCaseSql.startsWith("ROLLBACK") || upperCaseSql.startsWith("SELECT USER()")) {
                // LOG.info("Ignore sql {}", sqlToLog);
                return null;
            }

            final String spanName = SqlUtils.hideSqlValues(sqlToLog);
            // get parent span
            Span parentSpan = null;
            if (trace.getServiceContext() != null) {
                parentSpan = trace.getServiceContext().getSpan();
            } else if (trace.getWebContext() != null) {
                parentSpan = trace.getWebContext().getSpan();
            }
            // build span
            Span newSpan;
            if (parentSpan == null) {
                newSpan = trace.newSpan(spanName);
            } else {
                newSpan = trace.genSpan(parentSpan.getTraceId(),
                                        IdFactory.getInstance().getNextId(IdTypeEnums.SPAN_ID.getType(), spanName),
                                        parentSpan.getId(), parentSpan.newSubRpcId(), spanName, parentSpan.isSample());
            }

            if (!newSpan.isSample()) {
                return null;
            }
            newSpan.setAppType(trace.getType());
            newSpan.setAppName(trace.getAppName());
            newSpan.setItemType(TraceType.SQL.getType());
            newSpan.setHost(trace.loadEndPort());

            // annotate 'cs'
            Long startTime = System.currentTimeMillis();
            Annotation annotation = Spans.genAnnotation(Annotation.AnnType.CS, startTime);
            newSpan.addAnnotation(annotation);

            // save span
            TraceContext traceContext = new TraceContext();
            traceContext.setSpan(newSpan);
            trace.setDaoContext(traceContext);
        } catch (Throwable t) {
            LOG.error(t.getMessage(), t);
        }
        return null;
    }

    @Override
    public ResultSetInternalMethods postProcess(String sql, Statement statement,
                                                ResultSetInternalMethods resultSetInternalMethods,
                                                Connection connection, int i, boolean b, boolean b1, SQLException e)
                                                                                                                    throws SQLException {
        try {
            TraceContext traceContext = trace.getDaoContext();
            if (trace.isOn() && traceContext != null && traceContext.getSpan() != null) {
                Span span = traceContext.getSpan();
                // handle exception
                if (e != null) {
                    trace.logException(span, e);
                }
                // annotate 'cr'
                Long endTime = System.currentTimeMillis();
                Annotation annotation = Spans.genAnnotation(Annotation.AnnType.CR, endTime);
                span.addAnnotation(annotation);
                // log span
                trace.logSpan(span);
            }
        } catch (Throwable t) {
            LOG.error(t.getMessage(), t);
        } finally {
            trace.removeDaoContext();
        }
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return true;
    }

    @Override
    public void destroy() {

    }
}
