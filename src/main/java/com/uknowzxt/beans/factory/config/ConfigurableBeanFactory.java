package com.uknowzxt.beans.factory.config;

import com.uknowzxt.beans.factory.BeanFactory;

public interface ConfigurableBeanFactory extends BeanFactory{

    void setBeanClassLoader(ClassLoader beanClassLoader);
    ClassLoader getBeanClassLoader();
}
