/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

/**
 *
 *
 * @author panjin
 * @version $Id: ObjectLocator.java 2016年7月21日 下午5:10:54 $
 */
public interface ObjectLocator {

    /**
     * get bean by name
     * 
     * @param name bean name
     * @return
     */
    Object getObject(String name);

    /**
     * get bean by clazz
     * 
     * @param clazz clazz type
     * @return
     */
    Object getObject(Class<?> clazz);
}
