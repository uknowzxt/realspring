package com.uknowzxt.beans.factory.config;

import com.uknowzxt.beans.factory.BeanFactory;

public interface AutowireCapableBeanFactory extends BeanFactory {
	public Object resolveDependency(DependencyDescriptor descriptor);
}
