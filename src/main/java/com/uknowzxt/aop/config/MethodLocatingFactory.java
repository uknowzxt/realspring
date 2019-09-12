package com.uknowzxt.aop.config;

import com.uknowzxt.beans.BeanUtils;
import com.uknowzxt.beans.factory.BeanFactory;
import com.uknowzxt.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 对advice的抽象
 */
public class MethodLocatingFactory {
	
	private String targetBeanName;

	private String methodName;

	private Method method;
	
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	
	public void setBeanFactory(BeanFactory beanFactory) {
		if (!StringUtils.hasText(this.targetBeanName)) {
			throw new IllegalArgumentException("Property 'targetBeanName' is required");
		}
		if (!StringUtils.hasText(this.methodName)) {
			throw new IllegalArgumentException("Property 'methodName' is required");
		}

		Class<?> beanClass = beanFactory.getType(this.targetBeanName);//通过beanName获取这个Bean的Class
		if (beanClass == null) {
			throw new IllegalArgumentException("Can't determine type of bean with name '" + this.targetBeanName + "'");
		}
		
		
		this.method = BeanUtils.resolveSignature(this.methodName, beanClass);//通过class 和 方法名称得到这个方法

		if (this.method == null) {
			throw new IllegalArgumentException("Unable to locate method [" + this.methodName +
					"] on bean [" + this.targetBeanName + "]");
		}
	}


	public Method getObject() throws Exception {
		return this.method;
	}

}
