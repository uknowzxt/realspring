package com.uknowzxt.aop.aspectj;

import com.uknowzxt.aop.Advice;
import com.uknowzxt.aop.Pointcut;
import com.uknowzxt.aop.config.AspectInstanceFactory;

import java.lang.reflect.Method;

public abstract class AbstractAspectJAdvice implements Advice{
	protected Method adviceMethod;	
	protected AspectJExpressionPointcut pointcut;
	protected AspectInstanceFactory adviceObjectFactory;

	public AbstractAspectJAdvice(Method adviceMethod,
								 AspectJExpressionPointcut pointcut,
								 AspectInstanceFactory adviceObjectFactory){

		this.adviceMethod = adviceMethod;
		this.pointcut = pointcut;
		this.adviceObjectFactory = adviceObjectFactory;
	}

	public void invokeAdviceMethod() throws  Throwable{//通知实体类
		adviceMethod.invoke(adviceObjectFactory.getAspectInstance());
	}
	public Pointcut getPointcut(){
		return this.pointcut;
	}
	public Method getAdviceMethod() {
		return adviceMethod;
	}
	public Object getAdviceInstance() throws Exception {
		return adviceObjectFactory.getAspectInstance();
	}
}