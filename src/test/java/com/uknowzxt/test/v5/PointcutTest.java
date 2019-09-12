package com.uknowzxt.test.v5;

import org.junit.Assert;
import org.junit.Test;
import com.uknowzxt.aop.MethodMatcher;

import com.uknowzxt.aop.aspectj.AspectJExpressionPointcut;
import com.uknowzxt.service.v5.PetStoreService;

import java.lang.reflect.Method;


/**
 * 对切面的规则进行解析, 判断传入的方法符合规则吗
 */
public class PointcutTest {
	@Test
	public void testPointcut() throws Exception{
		
		String expression = "execution(* com.uknowzxt.service.v5.*.placeOrder(..))";
		
		AspectJExpressionPointcut pc = new AspectJExpressionPointcut();
		pc.setExpression(expression);
		
		MethodMatcher mm = pc.getMethodMatcher();
		
		{
			Class<?> targetClass = PetStoreService.class;
			
			Method method1 = targetClass.getMethod("placeOrder");		
			Assert.assertTrue(mm.matches(method1));
			
			Method method2 = targetClass.getMethod("getAccountDao");		
			Assert.assertFalse(mm.matches(method2));
		}
		
		{
			Class<?> targetClass = com.uknowzxt.service.v4.PetStoreService.class;			
		
			Method method = targetClass.getMethod("getAccountDao");		
			Assert.assertFalse(mm.matches(method));
		}
		
	}
}
