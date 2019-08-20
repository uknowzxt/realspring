package com.uknowzxt.test.v4;

import org.junit.Assert;
import org.junit.Test;
import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.factory.support.DefaultBeanFactory;
import com.uknowzxt.context.annotation.ClassPathBeanDefinitionScanner;
import com.uknowzxt.context.annotation.ScannedGenericBeanDefinition;
import com.uknowzxt.core.annotation.AnnotationAttributes;
import com.uknowzxt.core.type.AnnotationMetadata;
import com.uknowzxt.stereotype.Component;

//4 把包名下的文件转化成resource文件, 进而获取类的信息, 包括类的注解信息, 注解中存在component注解的时候, 把该类放入factory的beanDefinition中
public class ClassPathBeanDefinitionScannerTest {
	
	@Test
	public void testParseScanedBean(){
		
		DefaultBeanFactory factory = new DefaultBeanFactory();
		
		String basePackages = "com.uknowzxt.service.v4,com.uknowzxt.dao.v4";
		
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(factory);
		scanner.doScan(basePackages);
		
		
		String annotation = Component.class.getName();
		
		{
			BeanDefinition bd = factory.getBeanDefinition("petStore");
			Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
			ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)bd;
			AnnotationMetadata amd = sbd.getMetadata();
			
			
			Assert.assertTrue(amd.hasAnnotation(annotation));
			AnnotationAttributes attributes = amd.getAnnotationAttributes(annotation);
			Assert.assertEquals("petStore", attributes.get("value"));
		}
		{
			BeanDefinition bd = factory.getBeanDefinition("accountDao");
			Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
			ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)bd;			
			AnnotationMetadata amd = sbd.getMetadata();
			Assert.assertTrue(amd.hasAnnotation(annotation));
		}
		{
			BeanDefinition bd = factory.getBeanDefinition("itemDao");
			Assert.assertTrue(bd instanceof ScannedGenericBeanDefinition);
			ScannedGenericBeanDefinition sbd = (ScannedGenericBeanDefinition)bd;			
			AnnotationMetadata amd = sbd.getMetadata();
			Assert.assertTrue(amd.hasAnnotation(annotation));
		}
	}
}
