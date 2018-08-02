package com.uknowzxt.beans;

import com.sun.corba.se.impl.io.TypeMismatchException;

public interface TypeConverter {

    //根据传入的需要的类型，传入T，返回的就也是T类型
    <T> T convertIfNecessary(Object value,Class<T> requiredType) throws TypeMismatchException;
}
