package com.uknowzxt.beans;

import java.util.List;

public interface BeanDefinition {

     public static final String SCOPE_SINGLETON = "singleton";
     public static final String SCOPE_PROTOTYPE = "prototype";
     public static final String SCOPE_DEFAULT = "";



     public boolean isSingleton();
     public boolean isPrototype();

     String getScope();
     void setScope(String scope);



     String getBeanClassName() ;

     public List<PropertyValue> getPropertyValues();

     ConstructorArgument getConstructorArgument();

     public String getID();
     public boolean hasConstructorArgumentValues();

    Class<?> getBeanClass() throws IllegalStateException;
     boolean hasBeanClass();
     Class<?> resolveBeanClass(ClassLoader beanClassLoader) throws ClassNotFoundException;

    public boolean isSynthetic();
}
