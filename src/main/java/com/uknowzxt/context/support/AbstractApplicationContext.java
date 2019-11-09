package com.uknowzxt.context.support;

import com.uknowzxt.aop.aspectj.AspectJAutoProxyCreator;
import com.uknowzxt.beans.factory.NoSuchBeanDefinitionException;
import com.uknowzxt.beans.factory.annotation.AutowiredAnnotationProcessor;
import com.uknowzxt.beans.factory.config.ConfigurableBeanFactory;
import com.uknowzxt.beans.factory.support.DefaultBeanFactory;
import com.uknowzxt.beans.factory.xml.XmlBeanDefinitionReader;
import com.uknowzxt.context.ApplicationContext;
import com.uknowzxt.core.io.Resource;
import com.uknowzxt.util.ClassUtils;

import java.util.List;

public abstract class AbstractApplicationContext  implements ApplicationContext{
    private DefaultBeanFactory factory = null;
    private ClassLoader beanClassLoader;

    public AbstractApplicationContext(String configFile) {
        this(configFile,ClassUtils.getDefaultClassLoader());
    }

    public AbstractApplicationContext(String configFile,ClassLoader cl) {
        factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        Resource resource =this.getResourceByPath(configFile);
        reader.loadBeanDefinitions(resource);
        //todo 这个getBeanClassloader是一个null 这里用了两个构造函数方式
        //factory.setBeanClassLoader(this.getBeanClassLoader());
        factory.setBeanClassLoader(cl);
        registerBeanPostProcessors(factory);
    }


    @Override
    public Object getBean(String beanID) {
        return factory.getBean(beanID);
    }



    protected abstract Resource getResourceByPath(String path);


    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }


    public ClassLoader getBeanClassLoader() {
        return (this.beanClassLoader !=null?this.beanClassLoader: ClassUtils.getDefaultClassLoader());
    }
    protected void registerBeanPostProcessors(ConfigurableBeanFactory beanFactory) {

        {
            AutowiredAnnotationProcessor postProcessor = new AutowiredAnnotationProcessor();
            postProcessor.setBeanFactory(beanFactory);
            beanFactory.addBeanPostProcessor(postProcessor);
        }
        {
            AspectJAutoProxyCreator postProcessor = new AspectJAutoProxyCreator();
            postProcessor.setBeanFactory(beanFactory);
            beanFactory.addBeanPostProcessor(postProcessor);
        }

    }

    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return this.factory.getType(name);
    }
    public List<Object> getBeansByType(Class<?> type){
        return this.factory.getBeansByType(type);
    }

}
