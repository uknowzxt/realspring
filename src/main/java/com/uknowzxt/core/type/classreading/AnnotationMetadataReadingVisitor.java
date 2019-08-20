package com.uknowzxt.core.type.classreading;


import com.uknowzxt.core.annotation.AnnotationAttributes;
import com.uknowzxt.core.type.AnnotationMetadata;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Type;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 获取一个类的注解
 */
public class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements  AnnotationMetadata {
	
	private final Set<String> annotationSet = new LinkedHashSet<String>(4);//放入注解的名称的set集合
	private final Map<String, AnnotationAttributes> attributeMap = new LinkedHashMap<String, AnnotationAttributes>(4);//放入<注解名称,map<属性名,属性值>>的map集合
	
	public AnnotationMetadataReadingVisitor() {
		
	}

	/* *
	 *
	 * @param desc L表明它是一个object
	 * @param visible
	 * @return
	 */
	@Override
	public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
		
		String className = Type.getType(desc).getClassName();//获取注解的className
		this.annotationSet.add(className);
		return new AnnotationAttributesReadingVisitor(className, this.attributeMap);
	}
	public Set<String> getAnnotationTypes() {
		return this.annotationSet;
	}

    //从set中判断
	public boolean hasAnnotation(String annotationType) {
		return this.annotationSet.contains(annotationType);
	}

	//根据注解包名获取注解的属性
	public AnnotationAttributes getAnnotationAttributes(String annotationType) {
		return this.attributeMap.get(annotationType);
	}

	
	
}
