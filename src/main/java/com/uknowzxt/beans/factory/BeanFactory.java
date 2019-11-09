package com.uknowzxt.beans.factory;

import com.uknowzxt.beans.BeanDefinition;

import java.util.List;

public interface BeanFactory {


    Object getBean(String beanID);
    Class<?> getType(String name) throws NoSuchBeanDefinitionException;

    List<Object> getBeansByType(Class<?> type);
}
