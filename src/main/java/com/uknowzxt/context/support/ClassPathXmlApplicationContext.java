package com.uknowzxt.context.support;

import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.factory.support.DefaulBeanFactory;
import com.uknowzxt.beans.factory.xml.XmlBeanDefinitionReader;
import com.uknowzxt.context.ApplicationContext;
import com.uknowzxt.core.io.ClassPathResource;
import com.uknowzxt.core.io.Resource;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    public ClassPathXmlApplicationContext(String configFile){
        super(configFile);
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return new ClassPathResource(path,this.getBeanClassLoader() );
    }


}
