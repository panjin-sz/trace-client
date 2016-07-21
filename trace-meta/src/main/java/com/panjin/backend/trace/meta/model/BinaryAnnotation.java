/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.meta.model;

import java.io.Serializable;

/**
 *
 *
 * @author panjin
 * @version $Id: BinaryAnnotation.java 2016年7月21日 下午3:39:52 $
 */
public class BinaryAnnotation implements Serializable {

    /**  */
    private static final long serialVersionUID = 8393722369662055653L;
    private String key;
    private String value;
    private String type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BinaryAnnotation that = (BinaryAnnotation) o;

        if (key != null ? !key.equals(that.key) : that.key != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BinaryAnnotation{" + "key='" + key + '\'' + ", value='" + value + '\'' + ", type='" + type + '\'' + '}';
    }
}
