/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.meta.enums;

/**
 *
 *
 * @author panjin
 * @version $Id: ProblemEnums.java 2016年7月21日 下午3:32:44 $
 */
public enum ProblemEnums {

    PROBLEM_TYPE_LONG_URL("long-url"),

    PROBLEM_TYPE_LONG_SQL("long-sql"),

    PROBLEM_TYPE_LONG_SERVICE("long-service"),

    PROBLEM_TYPE_LONG_CALL("long-call"),

    PROBLEM_TYPE_EXCEPTION("exception"),

    FAILURE_COUNT("failureCount");

    public static final int[] LONG_URL_THRESHOLDS     = new int[] { 100, 200, 500, 1000, 1500, 2000, 3000, 5000 };
    public static final int[] LONG_SQL_THRESHOLDS     = new int[] { 100, 200, 500, 1000, 2000, 3000, 5000 };
    public static final int[] LONG_SERVICE_THRESHOLDS = new int[] { 50, 100, 200, 500, 1000, 2000, 3000, 5000 };
    public static final int[] LONG_CALL_THRESHOLDS    = new int[] { 50, 100, 200, 500, 1000, 2000, 3000, 5000 };

    private String type;

    ProblemEnums(String type) {
        this.type = type;
    }
    
    public String getType() {
        return type;
    }
}
