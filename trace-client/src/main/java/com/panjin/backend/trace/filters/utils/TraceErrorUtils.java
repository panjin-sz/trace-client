/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.common.json.JSON;
import com.google.common.base.Preconditions;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceErrorUtils.java 2016年7月21日 下午5:58:31 $
 */
public abstract class TraceErrorUtils {

    public static final String NEW_LINE = "<br>";

    public static final String TAB      = "at&nbsp;&nbsp;&nbsp;&nbsp;";

    /**
     * config Throwable stack trace
     * 
     * @param e
     * @return
     */
    public static String configStackTrace(Throwable e) {
        Preconditions.checkNotNull(e, "e is null!");

        try {
            String causeBy = "Caused by: " + e.toString();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(causeBy).append(NEW_LINE);
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                stringBuilder.append(TAB).append(stackTraceElement).append(NEW_LINE);
            }

            return stringBuilder.toString();
        } catch (Exception e1) {
            return "config error stack failed " + e1.toString();
        }
    }

    public static void main(String[] args) throws IOException {

        String hh = "111";
        try {
            hh.substring(10, 10);
        } catch (Exception e) {
            String s = TraceErrorUtils.configStackTrace(e);
            System.out.println(s);
        }

        String key = "aaa";
        String value = "b\"a";
        Map<String, String> test = new HashMap<String, String>();
        test.put(key, value);
        System.out.println(JSON.json(test).toString());
    }
}
