package com.uknowzxt.beans.factory.config;

import com.uknowzxt.beans.factory.BeanFactory;

import java.util.List;

public interface ConfigurableBeanFactory extends AutowireCapableBeanFactory{

    void setBeanClassLoader(ClassLoader beanClassLoader);
    ClassLoader getBeanClassLoader();
    void addBeanPostProcessor(BeanPostProcessor postProcessor);
    List<BeanPostProcessor> getBeanPostProcessors();
}
