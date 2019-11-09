package com.uknowzxt.beans.factory;

import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.factory.config.ConfigurableBeanFactory;
import com.uknowzxt.beans.factory.support.DefaultSingletonBeanRegistry;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {
	public abstract Object createBean(BeanDefinition bd) throws BeanCreationException;
}
