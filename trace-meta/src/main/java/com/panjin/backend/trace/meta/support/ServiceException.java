/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.meta.support;

/**
 *
 *
 * @author panjin
 * @version $Id: ServiceException.java 2016年7月21日 下午3:49:06 $
 */
public class ServiceException extends Exception {

    /**  */
    private static final long serialVersionUID = -6599806086959998255L;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
