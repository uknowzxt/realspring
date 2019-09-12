package com.uknowzxt.test.v5;

import org.junit.Assert;
import org.junit.Test;
import com.uknowzxt.aop.config.MethodLocatingFactory;
import com.uknowzxt.beans.factory.support.DefaultBeanFactory;
import com.uknowzxt.beans.factory.xml.XmlBeanDefinitionReader;
import com.uknowzxt.core.io.ClassPathResource;
import com.uknowzxt.core.io.Resource;
import com.uknowzxt.tx.TransactionManager;

import java.lang.reflect.Method;

/**
 * 找到要执行的动作 .
 */
public class MethodLocatingFactoryTest {
	@Test
	public void testGetMethod() throws Exception{
		DefaultBeanFactory beanFactory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
		Resource resource = new ClassPathResource("petstore-v5.xml");
		reader.loadBeanDefinitions(resource);
		
		MethodLocatingFactory methodLocatingFactory = new MethodLocatingFactory();
		methodLocatingFactory.setTargetBeanName("tx");
		methodLocatingFactory.setMethodName("start");
		methodLocatingFactory.setBeanFactory(beanFactory);//进行操作了
		
		Method m = methodLocatingFactory.getObject();//此时Object中已经放了一个method
		
		Assert.assertTrue(TransactionManager.class.equals(m.getDeclaringClass()));
		Assert.assertTrue(m.equals(TransactionManager.class.getMethod("start")));
		
	}
}
