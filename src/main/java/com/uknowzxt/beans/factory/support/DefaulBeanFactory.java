package com.uknowzxt.beans.factory.support;

import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.factory.BeanFactory;

public class DefaulBeanFactory implements BeanFactory {
    public DefaulBeanFactory(String configFile) { }

    @Override
    public BeanDefinition getBeanDefinition(String beanID) {
         return null;
    }

    @Override
    public Object getBean(String beanID) {
        return null;
    }
}
