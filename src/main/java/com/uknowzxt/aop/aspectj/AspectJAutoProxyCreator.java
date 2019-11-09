package com.uknowzxt.aop.aspectj;

import com.uknowzxt.aop.Advice;
import com.uknowzxt.aop.MethodMatcher;
import com.uknowzxt.aop.Pointcut;
import com.uknowzxt.aop.framework.AopConfigSupport;
import com.uknowzxt.aop.framework.AopProxyFactory;
import com.uknowzxt.aop.framework.CglibProxyFactory;
import com.uknowzxt.beans.BeansException;
import com.uknowzxt.beans.factory.config.BeanPostProcessor;
import com.uknowzxt.beans.factory.config.ConfigurableBeanFactory;
import com.uknowzxt.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AspectJAutoProxyCreator implements BeanPostProcessor {
	ConfigurableBeanFactory beanFactory;
	public Object beforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public Object afterInitialization(Object bean, String beanName) throws BeansException {
		
		//如果这个Bean本身就是Advice及其子类，那就不要再生成动态代理了。
		if(isInfrastructureClass(bean.getClass())){
			return bean;
		}
		
		List<Advice> advices = getCandidateAdvices(bean);//根据Advice.class 获取BeanDefinitions中存储的所有advice类.
		if(advices.isEmpty()){
			return bean;
		}
		
		return createProxy(advices,bean);
	}
	
	private List<Advice> getCandidateAdvices(Object bean){
		
		List<Object> advices = this.beanFactory.getBeansByType(Advice.class);
		
		List<Advice> result = new ArrayList<Advice>();
		for(Object o : advices){			
			Pointcut pc = ((Advice) o).getPointcut();
			if(canApply(pc,bean.getClass())){//判断当前bean在不在 当前pointcut所代表的expression中
				result.add((Advice) o);
			}
			
		}
		return result;
	}
	
	protected Object createProxy( List<Advice> advices ,Object bean) {
		AopConfigSupport config = new AopConfigSupport();
		for(Advice advice : advices){
			config.addAdvice(advice);
		}
		
		Set<Class> targetInterfaces = ClassUtils.getAllInterfacesForClassAsSet(bean.getClass());
		for (Class<?> targetInterface : targetInterfaces) {
			config.addInterface(targetInterface);
		}
		
		config.setTargetObject(bean);		
		
		AopProxyFactory proxyFactory = null;
		if(config.getProxiedInterfaces().length == 0){
			proxyFactory =  new CglibProxyFactory(config);
		} else{
			//TODO 需要实现JDK 代理
			//proxyFactory = new JdkAopProxyFactory(config);
		}	

		return proxyFactory.getProxy();
	}
	
	protected boolean isInfrastructureClass(Class<?> beanClass) {
		boolean retVal = Advice.class.isAssignableFrom(beanClass);
		
		return retVal;
	}
	
	public void setBeanFactory(ConfigurableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		
	}
	
	public static boolean canApply(Pointcut pc, Class<?> targetClass) {
		

		MethodMatcher methodMatcher = pc.getMethodMatcher();	

		Set<Class> classes = new LinkedHashSet<Class>(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
		classes.add(targetClass);
		for (Class<?> clazz : classes) {
			Method[] methods = clazz.getDeclaredMethods();			
			for (Method method : methods) {
				if (methodMatcher.matches(method/*, targetClass*/)) {
					return true;
				}
			}
		}

		return false;
	}

}
