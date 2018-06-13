package com.uknowzxt.beans.factory;

import com.uknowzxt.beans.BeanDefinition;

public interface BeanFactory {
    BeanDefinition getBeanDefinition(String beanID);

    Object getBean(String beanID);
}
