package com.uknowzxt.beans.factory;

import com.uknowzxt.beans.BeanDefinition;

public interface BeanFactory {


    Object getBean(String beanID);
}
