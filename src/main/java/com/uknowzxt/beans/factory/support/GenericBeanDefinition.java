package com.uknowzxt.beans.factory.support;


import com.uknowzxt.beans.BeanDefinition;

/**
 * 对应配置文件的id和class 类定义的pojo类
 */
public class GenericBeanDefinition implements BeanDefinition {
    private String id;
    private String beanClassName;

    public GenericBeanDefinition(String id, String beanClassName) {
        this.id = id;
        this.beanClassName = beanClassName;
    }

    @Override
    public String getBeanClassName() {
        return this.beanClassName;
    }
}
