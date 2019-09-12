package com.uknowzxt.aop;



public interface Pointcut {
	MethodMatcher getMethodMatcher();
	String getExpression();
}
