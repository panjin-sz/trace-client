/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 *
 *
 * @author panjin
 * @version $Id: JsonUtils.java 2016年7月21日 下午4:50:35 $
 */
public class JsonUtils {

    public static final Gson       LOWER_DASHES_GSON      = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).disableHtmlEscaping().create();
    public static final Gson       LOWER_UNDERSCORES_GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).disableHtmlEscaping().setDateFormat(Consts.TIME_FORMAT).create();
    public static final Gson       GSON                   = new Gson();
    public static final JsonParser JSON_PARSER            = new JsonParser();
}
