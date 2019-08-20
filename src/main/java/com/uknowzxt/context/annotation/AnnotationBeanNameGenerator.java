package com.uknowzxt.context.annotation;

import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.factory.annotation.AnnotatedBeanDefinition;
import com.uknowzxt.beans.factory.support.BeanDefinitionRegistry;
import com.uknowzxt.beans.factory.support.BeanNameGenerator;
import com.uknowzxt.core.annotation.AnnotationAttributes;
import com.uknowzxt.core.type.AnnotationMetadata;
import com.uknowzxt.util.ClassUtils;
import com.uknowzxt.util.StringUtils;

import java.beans.Introspector;
import java.util.Set;

public class AnnotationBeanNameGenerator implements BeanNameGenerator {


	public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		if (definition instanceof AnnotatedBeanDefinition) {
			String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);//如果注解的value属性中存在值,使用value属性的值作为名称
			if (StringUtils.hasText(beanName)) {
				// Explicit bean name found.
				return beanName;
			}
		}
		return buildDefaultBeanName(definition, registry);//否则以类名的小写作为beanName
	}

	/**
	 * Derive a bean name from one of the annotations on the class.
	 * @param annotatedDef the annotation-aware bean definition
	 * @return the bean name, or {@code null} if none is found
	 */
	protected String determineBeanNameFromAnnotation(AnnotatedBeanDefinition annotatedDef) {
		AnnotationMetadata amd = annotatedDef.getMetadata();
		Set<String> types = amd.getAnnotationTypes();//注解名称的set集合
		String beanName = null;
		for (String type : types) {
			AnnotationAttributes attributes = amd.getAnnotationAttributes(type);//<注解名称,map<属性名,属性值>> 中 根据注解名称取出属性值
			if (attributes.get("value") != null) {
				Object value = attributes.get("value");
				if (value instanceof String) {
					String strVal = (String) value;
					if (StringUtils.hasLength(strVal)) {						
						beanName = strVal;
					}
				}
			}
		}
		return beanName;
	}


	/**
	 * Derive a default bean name from the given bean definition.
	 * <p>The default implementation delegates to {@link #buildDefaultBeanName(BeanDefinition)}.
	 * @param definition the bean definition to build a bean name for
	 * @param registry the registry that the given bean definition is being registered with
	 * @return the default bean name (never {@code null})
	 */
	protected String buildDefaultBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
		return buildDefaultBeanName(definition);
	}

	/**
	 * Derive a default bean name from the given bean definition.
	 * <p>The default implementation simply builds a decapitalized version
	 * of the short class name: e.g. "mypackage.MyJdbcDao" -> "myJdbcDao".
	 * <p>Note that inner classes will thus have names of the form
	 * "outerClassName.InnerClassName", which because of the period in the
	 * name may be an issue if you are autowiring by name.
	 * @param definition the bean definition to build a bean name for
	 * @return the default bean name (never {@code null})
	 */
	protected String buildDefaultBeanName(BeanDefinition definition) {
		String shortClassName = ClassUtils.getShortName(definition.getBeanClassName());
		return Introspector.decapitalize(shortClassName);
	}

}

