package com.uknowzxt.test.v3;

import org.junit.Assert;
import org.junit.Test;
import com.uknowzxt.context.ApplicationContext;
import com.uknowzxt.context.support.ClassPathXmlApplicationContext;
import com.uknowzxt.service.v3.PetStoreService;

public class ApplicationContextTestV3 {

	@Test
	public void testGetBeanProperty() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("petstore-v3.xml");
		PetStoreService petStore = (PetStoreService)ctx.getBean("petStore");
		
		Assert.assertNotNull(petStore.getAccountDao());
		Assert.assertNotNull(petStore.getItemDao());		
		Assert.assertEquals(1, petStore.getVersion());
		
	}

}
