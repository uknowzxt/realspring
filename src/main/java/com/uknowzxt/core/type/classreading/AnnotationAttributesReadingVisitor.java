package com.uknowzxt.core.type.classreading;


import com.uknowzxt.core.annotation.AnnotationAttributes;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.SpringAsmInfo;

import java.util.Map;


/**
 * 实现对注解的属性解析
 */
final class AnnotationAttributesReadingVisitor extends AnnotationVisitor {

	private final String annotationType;

	private final Map<String, AnnotationAttributes> attributesMap;

	AnnotationAttributes attributes = new AnnotationAttributes();//本质是一个 放入<注解属性名,注解属性值>的LinkedHashMap


	public AnnotationAttributesReadingVisitor(
			String annotationType, Map<String, AnnotationAttributes> attributesMap) {
		super(SpringAsmInfo.ASM_VERSION);
		
		this.annotationType = annotationType;
		this.attributesMap = attributesMap;
		
	}
	@Override
	public final void visitEnd(){//访问结束的时候, 把注解属性的map, 放入attributesMap..(<注解类名,<属性名,属性值>>)
		this.attributesMap.put(this.annotationType, this.attributes);
	}
	
	public void visit(String attributeName, Object attributeValue) {//注解, 每有一个属性访问一次这个方法
		this.attributes.put(attributeName, attributeValue);
	}


}
