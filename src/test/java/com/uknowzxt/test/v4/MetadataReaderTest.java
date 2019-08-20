package com.uknowzxt.test.v4;

import org.junit.Assert;
import org.junit.Test;
import com.uknowzxt.core.annotation.AnnotationAttributes;
import com.uknowzxt.core.io.ClassPathResource;
import com.uknowzxt.core.type.AnnotationMetadata;
import com.uknowzxt.core.type.classreading.MetadataReader;
import com.uknowzxt.core.type.classreading.SimpleMetadataReader;
import com.uknowzxt.stereotype.Component;

import java.io.IOException;

//3 对使用ASM获取元数据的封装
public class MetadataReaderTest {
	@Test
	public void testGetMetadata() throws IOException{
		ClassPathResource resource = new ClassPathResource("com/uknowzxt/service/v4/PetStoreService.class");
		
		MetadataReader reader = new SimpleMetadataReader(resource);
		//注意：不需要单独使用ClassMetadata
		//ClassMetadata clzMetadata = reader.getClassMetadata();
		AnnotationMetadata amd = reader.getAnnotationMetadata();
		
		String annotation = Component.class.getName();
		
		Assert.assertTrue(amd.hasAnnotation(annotation));
		AnnotationAttributes attributes = amd.getAnnotationAttributes(annotation);		
		Assert.assertEquals("petStore", attributes.get("value"));
		
		//注：下面对class metadata的测试并不充分
		Assert.assertFalse(amd.isAbstract());
		Assert.assertFalse(amd.isFinal());
		Assert.assertEquals("com.uknowzxt.service.v4.PetStoreService", amd.getClassName());
		
	}
}
