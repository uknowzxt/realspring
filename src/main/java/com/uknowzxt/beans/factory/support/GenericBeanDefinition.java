package com.uknowzxt.beans.factory.support;


import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.ConstructorArgument;
import com.uknowzxt.beans.PropertyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * 对应配置文件的id和class 类定义的pojo类
 */
public class GenericBeanDefinition implements BeanDefinition {
    private String id;
    private String beanClassName;
    private boolean singleton = true;
    private boolean prototype = false;
    private String scope = SCOPE_DEFAULT;

    List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
    private ConstructorArgument constructorArgument = new ConstructorArgument();

    public GenericBeanDefinition(String id, String beanClassName) {

        this.id = id;
        this.beanClassName = beanClassName;
    }
    public GenericBeanDefinition() {

    }

    @Override
    public String getBeanClassName() {

        return this.beanClassName;
    }

    public void setBeanClassName(String className){
        this.beanClassName = className;
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

    @Override
    public ConstructorArgument getConstructorArgument() {
        return this.constructorArgument;
    }
    @Override
    public String getID() {
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }
    @Override
    public boolean hasConstructorArgumentValues() {
        return !this.constructorArgument.isEmpty();
    }

}
