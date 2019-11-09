package com.uknowzxt.aop.aspectj;

import com.uknowzxt.aop.config.AspectInstanceFactory;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;


public class AspectJBeforeAdvice extends AbstractAspectJAdvice {
	
	public AspectJBeforeAdvice(Method adviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory adviceObjectFactory){
		super(adviceMethod,pointcut,adviceObjectFactory);
	}
	
	public Object invoke(MethodInvocation mi) throws Throwable {//mi里面存在一个拦截器链
		//例如： 调用TransactionManager的start方法
		this.invokeAdviceMethod();
		Object o = mi.proceed();
		return o;
	}
}