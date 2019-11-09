package com.uknowzxt.aop.aspectj;


import org.aspectj.weaver.reflect.ReflectionWorld.ReflectionWorldException;
import org.aspectj.weaver.tools.*;
import com.uknowzxt.aop.MethodMatcher;
import com.uknowzxt.aop.Pointcut;
import com.uknowzxt.util.ClassUtils;
import com.uknowzxt.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class AspectJExpressionPointcut implements Pointcut,MethodMatcher {

	private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>();

	static {
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
		SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
	}

	private String expression;

	private  PointcutExpression pointcutExpression;

	private ClassLoader pointcutClassLoader;

	public AspectJExpressionPointcut(){

	}
	public MethodMatcher getMethodMatcher() {
		return this;
	}

	public String getExpression() {
		return this.expression;
	}
	public void setExpression(String expression){
		this.expression = expression;
	}


	public boolean matches(Method method/*, Class<?> targetClass*/) {

		checkReadyToMatch();//保证expression存在并且把expression转成PointcutExpression类

		ShadowMatch shadowMatch = getShadowMatch(method);//查看方法是否符合expression的要求

		if (shadowMatch.alwaysMatches()) {
			return true;
		}

		return false;
	}


	private ShadowMatch getShadowMatch(Method method) {

		ShadowMatch shadowMatch = null;
		try {
			shadowMatch = this.pointcutExpression.matchesMethodExecution(method);
		}
		catch (ReflectionWorldException ex) {

			throw new RuntimeException("not implemented yet");
			/*try {
				fallbackExpression = getFallbackPointcutExpression(methodToMatch.getDeclaringClass());
				if (fallbackExpression != null) {
					shadowMatch = fallbackExpression.matchesMethodExecution(methodToMatch);
				}
			}
			catch (ReflectionWorldException ex2) {
				fallbackExpression = null;
			}*/
		}
		return shadowMatch;
	}

	/**
	 * 判断expression是否存在
	 * 调用buildPointcutExpression,把expression字符串转成PointcutExpression类
	 */
	private void checkReadyToMatch() {
		if (getExpression() == null) {
			throw new IllegalStateException("Must set property 'expression' before attempting to match");
		}
		if (this.pointcutExpression == null) {
			this.pointcutClassLoader = ClassUtils.getDefaultClassLoader();
			this.pointcutExpression = buildPointcutExpression(this.pointcutClassLoader);
		}
	}

	/**
	 * 把expression字符串转成PointcutExpression类
	 * @param classLoader
	 * @return
	 */
	private PointcutExpression buildPointcutExpression(ClassLoader classLoader) {


		PointcutParser parser = PointcutParser
				.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
						SUPPORTED_PRIMITIVES, classLoader);
		
		/*PointcutParameter[] pointcutParameters = new PointcutParameter[this.pointcutParameterNames.length];
		for (int i = 0; i < pointcutParameters.length; i++) {
			pointcutParameters[i] = parser.createPointcutParameter(
					this.pointcutParameterNames[i], this.pointcutParameterTypes[i]);
		}*/
		return parser.parsePointcutExpression(replaceBooleanOperators(getExpression()),
				null, new PointcutParameter[0]);
	}


	private String replaceBooleanOperators(String pcExpr) {
		String result = StringUtils.replace(pcExpr, " and ", " && ");
		result = StringUtils.replace(result, " or ", " || ");
		result = StringUtils.replace(result, " not ", " ! ");
		return result;
	}

}