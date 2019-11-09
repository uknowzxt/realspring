package com.uknowzxt.test.v5;

import com.uknowzxt.aop.config.AspectInstanceFactory;
import com.uknowzxt.beans.factory.BeanFactory;
import com.uknowzxt.beans.factory.support.DefaultBeanFactory;
import com.uknowzxt.beans.factory.xml.XmlBeanDefinitionReader;
import com.uknowzxt.core.io.ClassPathResource;
import com.uknowzxt.core.io.Resource;
import com.uknowzxt.tx.TransactionManager;

import java.lang.reflect.Method;

public class AbstractV5Test {
		
	protected BeanFactory getBeanFactory(String configFile){
		DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(defaultBeanFactory);
		Resource resource = new ClassPathResource(configFile);
		reader.loadBeanDefinitions(resource);	
		return  defaultBeanFactory;		
	}
	
	protected  Method getAdviceMethod( String methodName) throws Exception{
		return TransactionManager.class.getMethod(methodName);		
	}
	
	protected  AspectInstanceFactory getAspectInstanceFactory(String targetBeanName){
		AspectInstanceFactory factory = new AspectInstanceFactory();
		factory.setAspectBeanName(targetBeanName);		
		return factory;
	}
	
	
}
