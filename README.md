# realspring
刘欣 从零开始造spring的学习


第二周：

一、课程目的：

能够使用applicationContext根据xml生成一个符合条件的bean的实例

二、要点

见类图



三、答疑要点

    1.BeanDefinition是一个内部类，不应该暴露registerBeanDefinition和setBeanDefinition方法给客户端。DefaulltBeanFactory实现了BeanDefinitionRegistry，那客户端不也可以调用getBeanDefinition（）和registerBeanDefinition吗？
        答：我们给用户使用的其实是Applicationcontext相关的类，虽然这个类依赖了DefaultBeanFactory，但是，并没有把factory的getBeanDefinition（）和registerBeanDefinition（）两个方法暴露出来。 
    
    2.
        （1）前置条件：在调用某一个方法之前，要确保某个条件
        （2）后置条件：在调用某一个方法之后，这个方法能保证什么东西
        （3）不变量：在这个过程中，不变的东西
    
    3.为什么要把Assert类设置成Abstract
        答：抽象类的实质是不让它实例化，因为Assert类本身是作为工具类的，是要告诉别人，直接用我的静态方法就好，不要把我new出来
    
    4.DefaultBeanFactory为什么要继承DefaultSingletonBeanRegistry，DefaultBeanFactory持有一个DefaultSingletonBeanRegistry实例也可以做到相同的效果。
        答：是可以的。
    
    5.spring+mybatis项目中如何mock数据
        答：其实应该mock Dao那一层 把接口做一层实现就好了。但是要权衡一下，在写测试用例的时候，不要单纯的只是测试框架了，要把自己的真实业务逻辑找出来，针对当前的业务逻辑来写测试用例
    
    6.根据单一职责原则，代码从一开始的BeanFactory，不断进行各个职责代码的抽取、分离到接口或者抽象类中。请问这里什么时候应该使用抽象类，什么时候应该用到接口呢？
        答：如果有各个子类共同的方法，就要使用抽象类，把共同的实现放在抽象类里面。只是对外定义的行为，那么就用接口。
        
    7.spring单例情况下，如果并发访问，同事调用一个对象的一个方法，会出问题吗？
        答：我们这里简单的spring没有对多线程进行处理，实际上的spring是加了锁的.
        
    8.XML文件当中别的标签，有不同的属性，要怎么处理解析呢？
        答：在XMLBeanDefinitionReader中进行解析。
        
    9.在定义beanDefinition的时候，关于类的scope，为什么是一个String类型，而不是一个常量类型。
        答：spring源码中没有使用常量，而是直接使用isSingleton()和isPropotype()来代替。在拿到scope中的类型字符串的时候，就对scope进行设置，并设置isSingleton()和isPropotype()，如果写错了，传进来的既不是singleton也不是prototype，那么就使用prototyoe.这样也算是增加了一些容错性。
        
    10. spring的注解是如何实现的
        答：注解本质是一个类，这个类可以作为一个元数据来标记到别的类的身上，我们可以写一个解析器，来读别的类的class文件，取出元数据。然后进行一些操作。之后会讲。
        
    11.记录一下分享的画类图的工具
        答：staruml 和 processon
        
    
      
        
                      
       
        
    
    