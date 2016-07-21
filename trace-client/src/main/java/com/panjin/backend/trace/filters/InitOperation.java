/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javassist.ClassPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInfoFactory;
import org.springframework.context.ApplicationContext;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.google.common.base.Preconditions;
import com.mysql.jdbc.Connection;
import com.panjin.backend.trace.common.utils.EnvInfo;
import com.panjin.backend.trace.common.utils.NetUtils;
import com.panjin.backend.trace.common.utils.TraceVersion;
import com.panjin.backend.trace.meta.model.Span;

/**
 *
 *
 * @author panjin
 * @version $Id: InitOperation.java 2016年7月21日 下午6:12:25 $
 */
public class InitOperation {

    private static final Logger     LOG          = LoggerFactory.getLogger(InitOperation.class);

    private static volatile boolean initialized  = false;

    private static final String     DEFAULT_PATH = "/META-INF/trace/trace-client.properties";

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        LOG.info(" ----------- start class file  duplicate check   -----------");

        final String configPath = System.getProperty("trace.properties", DEFAULT_PATH);

        TraceVersion.checkDuplicate(Logger.class);// sl4j
        TraceVersion.checkDuplicate(Preconditions.class);// guava
        TraceVersion.checkDuplicate(Span.class);// trace-meta
        TraceVersion.checkDuplicate(NetUtils.class);// trace-util
        TraceVersion.checkDuplicate(ApplicationConfig.class);// dubbo
        TraceVersion.checkDuplicate(ApplicationContext.class);// spring-context
        TraceVersion.checkDuplicate(BeanInfoFactory.class);// spring-bean
        TraceVersion.checkDuplicate("org.junit.Assume.class");// junit
        TraceVersion.checkDuplicate(Connection.class);// mysql
        TraceVersion.checkDuplicate(ClassPool.class);// javassist
        TraceVersion.checkDuplicate(DEFAULT_PATH);// properties
        TraceVersion.checkDuplicate(configPath);// properties

        LOG.info(" ----------- end class file  duplicate check   -----------");

        LOG.info(" ----------- start init config  -----------");
        InputStream propertiesInputStream = Trace.class.getResourceAsStream(configPath);

        Properties properties = new Properties();
        try {

            properties.load(propertiesInputStream);

            Trace.getInstance().init(properties);

        } catch (Exception e) {

            LOG.error(" ----------- init trace error !  -----------" + EnvInfo.getEnvInfo(), e);

            // 若是配置文件不存在的话，那么就全是默认值
            properties = new Properties();
            Trace.getInstance().init(properties);

        } finally {
            if (propertiesInputStream != null) {
                try {
                    propertiesInputStream.close();
                } catch (IOException e) {
                    LOG.error("close InputStream error . from : /META-INF/trace/trace-client.properties  "
                                      + EnvInfo.getEnvInfo(), e);
                }
            }
        }

        LOG.info(" ----------- end of  init trace  config  -----------");
        initialized = true;
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(Trace.class.getResourceAsStream("/META-INF/trace/trace-client111.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(properties.isEmpty());
    }
}
