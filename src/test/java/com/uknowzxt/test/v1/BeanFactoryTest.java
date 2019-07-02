package com.uknowzxt.test.v1;

import static org.junit.Assert.*;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.factory.BeanCreationException;
import com.uknowzxt.beans.factory.BeanDefinitionStoreException;
import com.uknowzxt.beans.factory.support.DefaultBeanFactory;
import com.uknowzxt.beans.factory.xml.XmlBeanDefinitionReader;
import com.uknowzxt.core.io.ClassPathResource;
import com.uknowzxt.service.v1.PetStoreService;

public class BeanFactoryTest {
	DefaultBeanFactory factory = null;
	XmlBeanDefinitionReader reader = null;
	
	@Before
	public void setUp(){
		factory = new DefaultBeanFactory();
		reader = new XmlBeanDefinitionReader(factory);
		
	}
	@Test
	public void testGetBean() {
		
		reader.loadBeanDefinitions(new ClassPathResource("petstore-v1.xml"));
		
		BeanDefinition bd = factory.getBeanDefinition("petStore");
		
		assertTrue(bd.isSingleton());
		
		assertFalse(bd.isPrototype());
		
		assertEquals(BeanDefinition.SCOPE_DEFAULT,bd.getScope());
		
		assertEquals("com.uknowzxt.service.v1.PetStoreService",bd.getBeanClassName());
		
		PetStoreService petStore = (PetStoreService)factory.getBean("petStore");
		
		assertNotNull(petStore);
		
		PetStoreService petStore1 = (PetStoreService)factory.getBean("petStore");
		
		assertTrue(petStore.equals(petStore1));
	}
	
	@Test
	public void testInvalidBean(){

		reader.loadBeanDefinitions(new ClassPathResource("petstore-v1.xml"));
		try{
			factory.getBean("invalidBean");
		}catch(BeanCreationException e){
			return;
		}
		Assert.fail("expect BeanCreationException ");
	}
	@Test
	public void testInvalidXML(){
		
		try{
			reader.loadBeanDefinitions(new ClassPathResource("xxxx.xml"));
		}catch(BeanDefinitionStoreException e){
			return;
		}
		Assert.fail("expect BeanDefinitionStoreException ");
	}

}
