package com.uknowzxt.context.annotation;

import com.uknowzxt.beans.factory.annotation.AnnotatedBeanDefinition;
import com.uknowzxt.beans.factory.support.GenericBeanDefinition;
import com.uknowzxt.core.type.AnnotationMetadata;

public class ScannedGenericBeanDefinition extends GenericBeanDefinition implements AnnotatedBeanDefinition {

	private final AnnotationMetadata metadata;//除了正常的类的定义外,还有注解相关的数据


	public ScannedGenericBeanDefinition(AnnotationMetadata metadata) {
		super();
		
		this.metadata = metadata;
		
		setBeanClassName(this.metadata.getClassName());//类的名称
	}


	public final AnnotationMetadata getMetadata() {
		return this.metadata;
	}

}