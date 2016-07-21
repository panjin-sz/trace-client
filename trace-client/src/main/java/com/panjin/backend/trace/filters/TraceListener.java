/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.filters;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author panjin
 * @version $Id: TraceListener.java 2016年7月21日 下午6:16:35 $
 */
public class TraceListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(TraceListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        LOG.info("init by TraceListener");
        InitOperation.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Trace.getInstance().destroy();
    }
}
