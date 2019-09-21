package com.uknowzxt.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveMethodInvocation implements MethodInvocation {

	
	
	protected final Object targetObject; //petStoreService

	protected final Method targetMethod; //placeOrder方法

	protected Object[] arguments;

	

	/**
	 * List of MethodInterceptor 
	 */
	protected final List<MethodInterceptor> interceptors;

	/**
	 * Index from 0 of the current interceptor we're invoking.
	 * -1 until we invoke: then the current interceptor.
	 */
	private int currentInterceptorIndex = -1;


	public ReflectiveMethodInvocation(
			Object target, Method method, Object[] arguments,
			List<MethodInterceptor> interceptors) {

		this.targetObject = target;		//PestService
		this.targetMethod = method;    //PlaceOrder
		this.arguments = arguments;     //方法参数
		this.interceptors = interceptors;  //参与拦截的拦截器
	}



	public final Object getThis() {
		return this.targetObject;
	}


	/**
	 * Return the method invoked on the proxied interface.
	 * May or may not correspond with a method invoked on an underlying
	 * implementation of that interface.
	 */
	public final Method getMethod() {
		return this.targetMethod;
	}

	public final Object[] getArguments() {
		return (this.arguments != null ? this.arguments : new Object[0]);
	}


	/**
	 * 链式调用:保证拦截器的顺序
	 * (1)只有最后一次递归调用, 才会进入执行目标方法
	 *    其他时候仅仅是去一个接着一个去调用拦截器
	 * (2)拦截器中如果执行了递归调用, 则自己会马上挂起, 知道所有拦截器都走完, 才会一个个的调用回来.
	 * @return
	 * @throws Throwable
	 */
	public Object proceed() throws Throwable {
		//	所有的拦截器已经调用完成
		if (this.currentInterceptorIndex == this.interceptors.size() - 1) {
			return invokeJoinpoint();
		}

		this.currentInterceptorIndex ++;
		
		MethodInterceptor interceptor =
				this.interceptors.get(this.currentInterceptorIndex);
		
		return interceptor.invoke(this);
		
	}

	/**
	 * Invoke the joinpoint using reflection.
	 * Subclasses can override this to use custom invocation.
	 * @return the return value of the joinpoint
	 * @throws Throwable if invoking the joinpoint resulted in an exception
	 */
	protected Object invokeJoinpoint() throws Throwable {		
		return this.targetMethod.invoke(this.targetObject, this.arguments);
	}

	public AccessibleObject getStaticPart() {		
		return this.targetMethod;
	}
	
}


