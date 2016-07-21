/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters.utils;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceFilterUtils.java 2016年7月21日 下午5:59:42 $
 */
public abstract class TraceFilterUtils {
    private TraceFilterUtils() {
    }

    public static Long getAttachmentLong(String value) {
        if (isNull(value)) {
            return null;
        }
        return Long.valueOf(value);
    }

    public static Boolean getAttachmentBoolean(String value) {
        if (isNull(value)) {
            return false;
        }
        return Boolean.valueOf(value);
    }

    private static boolean isNull(String value) {
        if (value == null || value.length() == 0 || "null".equalsIgnoreCase(value.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 过滤引号
     * 
     * @param value
     * @return
     */
    public static String jsonFilter(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }

        return value.replaceAll("\\\"", "").replaceAll("\\\'", "").replaceAll(":", "").replaceAll("]", "").replaceAll("\\[", "").replaceAll("\\{", "")
                .replaceAll("\\}", "");
    }

    public static void main(String[] args) {
        String value = "Exception executing consequence for rule \"2014102910R137\" 'in'  :  com.netease.ruleengine.riskOrderRiskRule: java.lang.NullPointerException";

        // String s = TraceFilterUtils.jsonFilter(value);
        // System.out.println(s);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(1418370151765l);

        System.out.println(1418370151765l + ":" + calendar.getTime().toString());

        calendar.setTimeInMillis(1418370152288l);

        System.out.println(1418370152288l + ":" + calendar.getTime().toString());

    }
}
