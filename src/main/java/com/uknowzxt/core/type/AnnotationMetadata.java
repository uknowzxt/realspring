package com.uknowzxt.core.type;

import com.uknowzxt.core.annotation.AnnotationAttributes;
import com.uknowzxt.core.annotation.AnnotationAttributes;

import java.util.Set;

//注解元数据
public interface AnnotationMetadata extends ClassMetadata{
	
	Set<String> getAnnotationTypes();


	boolean hasAnnotation(String annotationType);
	
	public AnnotationAttributes getAnnotationAttributes(String annotationType);
}
