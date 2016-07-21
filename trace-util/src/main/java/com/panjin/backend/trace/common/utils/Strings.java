/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 *
 *
 * @author panjin
 * @version $Id: Strings.java 2016年7月21日 下午4:52:24 $
 */
public abstract class Strings {

    /**
     * 空字符串
     */
    public static final String EMPTY = " ";

    public static String replaceTabAndLineFeedCharacter(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }

        return text.replaceAll("\\\r", EMPTY).replaceAll("\\\n", EMPTY).replaceAll("\\\t", EMPTY);
    }
}
