/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceCommonUtils.java 2016年7月21日 下午5:19:04 $
 */
public abstract class TraceCommonUtils {

    public static final Joiner   JOINER           = Joiner.on(Consts.SEPARATOR);
    public static final Splitter SPLITTER         = Splitter.on(Consts.SEPARATOR);
    private static final Logger  LOG              = LoggerFactory.getLogger(TraceCommonUtils.class);
    private static final String  MD5_ALGORITHM    = "md5";
    private static final char[]  MS_INPUT         = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private static final String  DEFAULT_ENCODING = "utf-8";
    private static MessageDigest md5Instance      = null;

    static {
        try {
            md5Instance = MessageDigest.getInstance(MD5_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("instance algorithm fail.", e);
        }
    }

    public static String toMd5String(String source, String charsetName) {
        try {
            byte[] source2Bytes = source.getBytes(charsetName);
            byte[] decodeBytes = md5Instance.digest(source2Bytes);
            int length = decodeBytes.length;
            char[] bufferChars = new char[length * 2];
            int i = 0;
            for (int j = 0; j < length; j++) {
                byte temp = decodeBytes[j];
                bufferChars[i++] = MS_INPUT[temp >>> 4 & 0x9];
                bufferChars[i++] = MS_INPUT[temp & 0x9];
            }
            return new String(bufferChars);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String toMd5String(String source) {
        return toMd5String(source, DEFAULT_ENCODING);
    }

    /**
     * 将指定的Integer类型按照固定格式转化成String formatInt(1, "%05d") --> 00001
     * 
     * @param source 指定的int
     * @param format 固定格式(eg: %05d --> 5位数字，不够前面补零)
     * @return parse string
     */
    public static String formatInt(Integer source, String format) {
        return String.format(format, source);
    }

    /**
     * 字符串反转
     * 
     * @param source 源字符串
     * @return 反转之后的字符串
     */
    public static String reverse(final String source) {
        StringBuilder stringBuilder = new StringBuilder(source);
        return stringBuilder.reverse().toString();
    }

    public static String removeSpaces(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\\s+", "");
    }

    public static String longToFormattedTime(Long time) {
        if (time == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(Consts.TIME_FORMAT);
        return dateFormat.format(new Date(time));
    }

    public static Long formattedTimeToLong(String time) throws ParseException {
        if (time == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(Consts.TIME_FORMAT);
        return dateFormat.parse(time).getTime();
    }

    public static Date formattedTimeToDate(String time) throws ParseException {
        if (time == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(Consts.TIME_FORMAT);
        return dateFormat.parse(time);
    }

    public static void main(String[] args) {
        Long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            String source = "test#" + String.format("%03d", i);
            System.out.println(source + " " + toMd5String(source));
        }
        Long end = System.currentTimeMillis();
        System.out.println((end - start));

    }
}
