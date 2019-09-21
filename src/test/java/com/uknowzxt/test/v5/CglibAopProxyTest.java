package com.uknowzxt.test.v5;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.uknowzxt.aop.aspectj.AspectJAfterReturningAdvice;
import com.uknowzxt.aop.aspectj.AspectJBeforeAdvice;
import com.uknowzxt.aop.aspectj.AspectJExpressionPointcut;
import com.uknowzxt.aop.framework.AopConfig;
import com.uknowzxt.aop.framework.AopConfigSupport;
import com.uknowzxt.aop.framework.CglibProxyFactory;
import com.uknowzxt.service.v5.PetStoreService;
import com.uknowzxt.tx.TransactionManager;
import com.uknowzxt.util.MessageTracker;

import java.util.List;


public class CglibAopProxyTest {
		
	private static AspectJBeforeAdvice beforeAdvice = null;
	private static AspectJAfterReturningAdvice afterAdvice = null;
	private static AspectJExpressionPointcut pc = null;
	private static AspectJBeforeAdvice beforeAdvice2 = null;
	private static AspectJAfterReturningAdvice afterAdvice2 = null;
	private static AspectJExpressionPointcut pc2 = null;
	
	private TransactionManager tx;
	
	@Before
	public  void setUp() throws Exception{		
		
		
		tx = new TransactionManager();
		String expression = "execution(* com.uknowzxt.service.v5.*.placeOrder(..))";
		pc = new AspectJExpressionPointcut();
		pc.setExpression(expression);
		
		beforeAdvice = new AspectJBeforeAdvice(
				TransactionManager.class.getMethod("start"),//通知要执行的方法
				pc,//切点规则
				tx);//通知要执行的类
		
		afterAdvice = new AspectJAfterReturningAdvice(
				TransactionManager.class.getMethod("commit"),//通知要执行的方法
				pc,//切点规则
				tx);	//通知要执行的类


		//与即将要执行的方法不相符的规则
		String expression2 = "execution(* com.uknowzxt.service.v5.*.placeOrder2(..))";
		pc = new AspectJExpressionPointcut();
		pc.setExpression(expression2);

		beforeAdvice2 = new AspectJBeforeAdvice(
				TransactionManager.class.getMethod("start"),//通知要执行的方法
				pc2,//切点规则
				tx);//通知要执行的类

		afterAdvice2 = new AspectJAfterReturningAdvice(
				TransactionManager.class.getMethod("commit"),//通知要执行的方法
				pc2,//切点规则
				tx);	//通知要执行的类
		
	}
	
	@Test
	public void testGetProxy() throws Exception{

		
		AopConfig config = new AopConfigSupport();//aop的配置: 里面有通知的集合\目标类
		
		config.addAdvice(beforeAdvice);
		config.addAdvice(afterAdvice);
		config.addAdvice(beforeAdvice2);
		config.addAdvice(afterAdvice2);
		config.setTargetObject(new PetStoreService());
		
		
		CglibProxyFactory proxyFactory = new CglibProxyFactory(config);
		
		PetStoreService proxy = (PetStoreService)proxyFactory.getProxy();//获取代理对象
		
		proxy.placeOrder();				
		
		
		List<String> msgs = MessageTracker.getMsgs();
		Assert.assertEquals(3, msgs.size());
		Assert.assertEquals("start tx", msgs.get(0));	
		Assert.assertEquals("place order", msgs.get(1));	
		Assert.assertEquals("commit tx", msgs.get(2));	
		
		proxy.toString();
	}
	
	
	
	
}
