/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.concurrent;

import java.util.concurrent.*;

/**
 *
 *
 * @author panjin
 * @version $Id: ExecutorServiceFactory.java 2016年7月21日 下午5:25:29 $
 */
public class ExecutorServiceFactory {

    private static final int DEFAULT_CORE_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    private static final int DEFAULT_MAX_SIZE  = Runtime.getRuntime().availableProcessors() * 2;

    private static final int DEFAULT_LIVE_TIME = 1000;

    private static final int QUEUE_SIZE        = 20000;

    /**
     * 自定义名字的线程池,默认策略是抛弃最新的数据
     * 
     * @param name
     *            名字
     * @return
     */
    public static ThreadPoolExecutor builderDefault(String name) {
        return builder(DEFAULT_CORE_SIZE, DEFAULT_MAX_SIZE, DEFAULT_LIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(QUEUE_SIZE),
                new NamedThreadFactory(name, true), new ThreadPoolExecutor.DiscardPolicy());
    }

    /**
     * 自定义名字及队列溢出策略的线程池
     * 
     * @param name
     *            名字
     * @param handler
     *            溢出策略
     * @return
     */
    public static ThreadPoolExecutor builderDefault(String name, RejectedExecutionHandler handler) {
        return builder(DEFAULT_CORE_SIZE, DEFAULT_MAX_SIZE, DEFAULT_LIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(QUEUE_SIZE),
                new NamedThreadFactory(name, true), handler);
    }

    public static ThreadPoolExecutor builder(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue,
            NamedThreadFactory threadFactory, RejectedExecutionHandler handler) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

}
