/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 *
 *
 * @author panjin
 * @version $Id: EnvInfo.java 2016年7月21日 下午4:35:29 $
 */
public class EnvInfo {

    private static final Logger LOG = LoggerFactory.getLogger(EnvInfo.class);

    private static final Joiner JOINER = Joiner.on("-").skipNulls();

    /**
     * 获取环境信息
     * 
     * @return
     */
    public static String getEnvInfo() {
        String result = null;
        try {
            result = JOINER.join(new String[] { InetAddress.getLocalHost().toString(), TraceVersion.getVersion() });
        } catch (UnknownHostException e) {
            LOG.error("get env info error ", e);
        }

        return result;

    }
}
