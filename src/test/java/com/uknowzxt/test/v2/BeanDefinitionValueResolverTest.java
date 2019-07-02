package com.uknowzxt.test.v2;

import org.junit.Assert;
import org.junit.Test;
import com.uknowzxt.beans.factory.config.RuntimeBeanReference;
import com.uknowzxt.beans.factory.config.TypedStringValue;
import com.uknowzxt.beans.factory.support.BeanDefinitionValueResolver;
import com.uknowzxt.beans.factory.support.DefaultBeanFactory;
import com.uknowzxt.beans.factory.xml.XmlBeanDefinitionReader;
import com.uknowzxt.core.io.ClassPathResource;
import com.uknowzxt.dao.v2.AccountDao;

public class BeanDefinitionValueResolverTest {

	@Test
	public void testResolveRuntimeBeanReference() {
		DefaultBeanFactory factory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);		
		reader.loadBeanDefinitions(new ClassPathResource("petstore-v2.xml"));
		
		BeanDefinitionValueResolver resolver = new BeanDefinitionValueResolver(factory);
		
		RuntimeBeanReference reference = new RuntimeBeanReference("accountDao");
		Object value = resolver.resolveValueIfNecessary(reference);
		
		Assert.assertNotNull(value);		
		Assert.assertTrue(value instanceof AccountDao);				
	}
	@Test
	public void testResolveTypedStringValue() {
		DefaultBeanFactory factory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);		
		reader.loadBeanDefinitions(new ClassPathResource("petstore-v2.xml"));
		
		BeanDefinitionValueResolver resolver = new BeanDefinitionValueResolver(factory);
		
		TypedStringValue stringValue = new TypedStringValue("test");
		Object value = resolver.resolveValueIfNecessary(stringValue);		
		Assert.assertNotNull(value);		
		Assert.assertEquals("test", value);
		
	}
}
