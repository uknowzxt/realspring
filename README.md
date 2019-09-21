# realspring

@aop(二) 见类图


##一、目的：
(1)根据expression字符串,判断当前执行的方法是不是符合条件的
(2)找出增强时, 调用的方法.

*** 

##二、要点

###1.AspectJExpressionPointcut 找出pointCut  

    目的: 从<aop:pointcut>的expression中解析出AspectJ包下的PointcutExpression, 利用这个类来判断传入的方法是否符合规则
   1.继承体系:
   
            Pointcut[MethodMatcher getMethodMatcher();       MethodMatcher[boolean matches(Method method/*, Class<?> targetClass*/);]
                     String getExpression();]   
                            👆实现                                                       👆实现
                                    AspectJExpressionPointcut[private String expression;
                                                              private  PointcutExpression pointcutExpression;
                                                              private ClassLoader pointcutClassLoader;]
                                                              
   2.主要方法:matches(Method method/*, Class<?> targetClass*/) 
   
        (1)调用checkReadyToMatch();方法, 确保expression存在, 并且把expression字符串转化成PointcutExpression类
            [1]调用getExpression查看expression是不是空, 是空的话抛出异常
            [2]查看成员变量pointcutExpression是否已经被赋值,没有的话调用buildPointcutExpression(ClassLoader classLoader)把expression字符串转化成PointcutExpression
        (2)调用getShadowMatch(method)方法, 获得ShadowMatch.
        (3)利用ShadowMatch来判断是不是符合.
        
        '''java
        checkReadyToMatch();//保证expression存在并且把expression转成PointcutExpression类
        		
        		ShadowMatch shadowMatch = getShadowMatch(method);//查看方法是否符合expression的要求
        		
        		if (shadowMatch.alwaysMatches()) {
        			return true;
        		}
        		
        		return false;
        '''java
          
             
             
   
###2.MethodLocatingFactory 定位增强时调用的方法
    目的: 找到通知. 通过类和方法名, 找到增强时要执行的方法. 
    
   1 抽象通知-MethodLocatingFactory
    
       private String targetBeanName;
       private String methodName;
       private Method method;
              
  2 关键方法-setBeanFactory(BeanFactory beanFactory)
        
        (1)判断targetBeanName有没有值, 没有的话抛出错误
        (2)判断methodName有没有值, 没有的话抛出错误
        (3)调用beanFactory的getType(this.targetBeanName)方法, 获得bean的Class类
                [1]BeanFacotry接口中加入新的方法, getType(this.targetBeanName).
                [2]getType方法首先通过BeanName获取BeanDefinition.
                [3]调用resolveBeanClass(bd)来确保bd中存在这个bean的class对象
                [4]调用BeanDefinition.getBeanClass()得到Class类并返回
        (4)调用BeanUtils.resolveSignature(this.methodName, beanClass). 来得到这份方法, 并给成员变量Method method赋值

