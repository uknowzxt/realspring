package com.uknowzxt.test.v4;

import org.junit.Assert;
import org.junit.Test;
import com.uknowzxt.context.ApplicationContext;
import com.uknowzxt.context.support.ClassPathXmlApplicationContext;
import com.uknowzxt.service.v4.PetStoreService;

public class ApplicationContextTest4 {

	@Test
	public void testGetBeanProperty() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("petstore-v4.xml");
		PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");
		
		Assert.assertNotNull(petStore.getAccountDao());
		Assert.assertNotNull(petStore.getItemDao());
		
	}	
}
