package com.uknowzxt.context.support;

import com.uknowzxt.beans.factory.support.DefaultBeanFactory;
import com.uknowzxt.beans.factory.xml.XmlBeanDefinitionReader;
import com.uknowzxt.context.ApplicationContext;
import com.uknowzxt.core.io.Resource;
import com.uknowzxt.util.ClassUtils;

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
    }


    @Override
    public Object getBean(String beanID) {
        return factory.getBean(beanID);
    }



    protected abstract Resource getResourceByPath(String path);

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return (this.beanClassLoader !=null?this.beanClassLoader: ClassUtils.getDefaultClassLoader());
    }

}
