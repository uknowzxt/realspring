package com.uknowzxt.test.v4;

import org.junit.Assert;
import org.junit.Test;
import com.uknowzxt.beans.factory.annotation.AutowiredFieldElement;
import com.uknowzxt.beans.factory.annotation.InjectionElement;
import com.uknowzxt.beans.factory.annotation.InjectionMetadata;
import com.uknowzxt.beans.factory.support.DefaultBeanFactory;
import com.uknowzxt.beans.factory.xml.XmlBeanDefinitionReader;
import com.uknowzxt.core.io.ClassPathResource;
import com.uknowzxt.core.io.Resource;
import com.uknowzxt.dao.v4.AccountDao;
import com.uknowzxt.dao.v4.ItemDao;
import com.uknowzxt.service.v4.PetStoreService;

import java.lang.reflect.Field;
import java.util.LinkedList;

public class InjectionMetadataTest {

	@Test
	public void testInjection() throws Exception{
		
		DefaultBeanFactory factory = new DefaultBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		Resource resource = new ClassPathResource("petstore-v4.xml");
		reader.loadBeanDefinitions(resource);
		
		Class<?> clz = PetStoreService.class;
		LinkedList<InjectionElement> elements = new LinkedList<InjectionElement>();
		
		{
			Field f = PetStoreService.class.getDeclaredField("accountDao");		
			InjectionElement injectionElem = new AutowiredFieldElement(f,true,factory);
			elements.add(injectionElem);
		}
		{
			Field f = PetStoreService.class.getDeclaredField("itemDao");		
			InjectionElement injectionElem = new AutowiredFieldElement(f,true,factory);
			elements.add(injectionElem);
		}
		
		InjectionMetadata metadata = new InjectionMetadata(clz,elements);
		
		PetStoreService petStore = new PetStoreService();
		
		metadata.inject(petStore);
		
		Assert.assertTrue(petStore.getAccountDao() instanceof AccountDao);
		
		Assert.assertTrue(petStore.getItemDao() instanceof ItemDao);
		
	}
}
