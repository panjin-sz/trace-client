/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import org.springframework.context.ApplicationContext;

import com.google.common.base.Preconditions;

/**
 *
 *
 * @author panjin
 * @version $Id: SpringObjectLocator.java 2016年7月21日 下午5:13:38 $
 */
public abstract class SpringObjectLocator implements ObjectLocator {

    protected ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    /** 
     * @see com.panjin.backend.trace.common.utils.ObjectLocator#getObject(java.lang.String)
     */
    @Override
    public Object getObject(String name) {
        Preconditions.checkNotNull(applicationContext, "applicationContext is null, please init spring first!");
        return applicationContext.getBean(name);
    }

    /** 
     * @see com.panjin.backend.trace.common.utils.ObjectLocator#getObject(java.lang.Class)
     */
    @Override
    public Object getObject(Class<?> clazz) {
        Preconditions.checkNotNull(applicationContext, "applicationContext is null, please init spring first!");
        return applicationContext.getBean(clazz);
    }

}
