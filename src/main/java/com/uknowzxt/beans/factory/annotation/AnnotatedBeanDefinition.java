package com.uknowzxt.beans.factory.annotation;

import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.core.type.AnnotationMetadata;

public interface AnnotatedBeanDefinition extends BeanDefinition {
	AnnotationMetadata getMetadata();
}
