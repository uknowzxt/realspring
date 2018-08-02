package com.uknowzxt.beans.factory.support;


import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.PropertyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 对应配置文件的id和class 类定义的pojo类
 */
public class  GenericBeanDefinition implements BeanDefinition {
    private String id;
    private String beanClassName;
    private boolean singleton = true;
    private boolean prototype = false;
    private String scope = SCOPE_DEFAULT;

    List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();

    public GenericBeanDefinition(String id, String beanClassName) {

        this.id = id;
        this.beanClassName = beanClassName;
    }

    @Override
    public String getBeanClassName() {

        return this.beanClassName;
    }


    @Override
    public boolean isSingleton() {
        return this.singleton;
    }

    @Override
    public boolean isPrototype() {
        return this.prototype;
    }
    @Override
    public String getScope() {
        return this.scope;
    }
    @Override
    public void setScope(String scope) {
        this.scope = scope;
        this.singleton = SCOPE_SINGLETON.equals(scope) || SCOPE_DEFAULT.equals(scope);
        this.prototype = SCOPE_PROTOTYPE.equals(scope);

    }

    @Override
    public List<PropertyValue> getPropertyValues() {
        return this.propertyValues;
    }

}
