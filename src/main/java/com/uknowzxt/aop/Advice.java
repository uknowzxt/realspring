package com.uknowzxt.aop;

import org.aopalliance.intercept.MethodInterceptor;

public interface Advice extends MethodInterceptor{
	public Pointcut getPointcut();
}

