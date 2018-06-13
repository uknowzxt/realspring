# realspring
刘欣 从零开始造spring的学习


第一周：

一、课程目的：

使用TDD模式，给定一个xml配置文件（内含bean的定义），能够从中获取：
1.Bean的定义
2.Bean的实例

二、要点

1 各个类的大体功能：
    
    （1）BeanDefinition 接口 ，类定义接口。 
    （2）实现BeanDefinition接口，有 id和beanClassName 两个成员属性
    （3）BeanFactory 接口 ，用来获取类定义（getBeanDefinition）和类的实现（getBean）
    （4）DefaulBeanFactory ，BeanFactory的实现类。
            有参构造   ： public BeanDefinition getBeanDefinition(String beanId) 直接调用void loadBeanDefinition(String configFile) 方法
            方法      ： loadBeanDefinition(String configFile) 解析xml，并且为成员变量Map<String,BeanDefinition> beanDefinitionMap 赋值
            方法      ： BeanDefinition getBeanDefinition(String beanId)，从成员变量beanDefinitionMap中根据beanId获取该类BeanDefinition对象
            方法      ： Object getBean(String beanId) ，调用getBeanDefinition获取到BeanDefinition对象，从BeanDefinition对象中获取BeanClassName属性，根据这个属性，运用反射，创建实例
    



2 本次课程的异常继承体系

                        RuntimeException

                             ↑

                         BeansException

        ↑                                           ↑
  
    BeanDefinitionStoreException             BeanCreationExceptin
    
        ↑                                           ↑
    读取XML文件出错时抛出异常                  创建Bean出错时抛出异常
    



