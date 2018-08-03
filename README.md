# realspring
刘欣 从零开始造spring的学习


第二周：

一、课程目的：

能够使用applicationContext根据xml生成一个bean的实例，并把xml中bean的<property>属性设置给bean

二、要点

见思维导图



三、答疑要点

    1、ref如何保证从factory获取类的时候，ref的对象已经加载放到类factory的map中呢？
    答：spring在处理这个问题的时候，保证了在加载某一个类的时候，会把这个类的属性同时加载进来。
    
    2、两个bean相互引用，是不是就会出现循环引用
    答：spring对此进行了处理，如果spring在发现依赖会把依赖打印到控制台上面。
    
    3、set的反射为什么不是直接拼接方法反射待处理（有更简单的处理）　
    答：主要是为了使用javaBean的特性。----commonBeansUtil
    
    
    
        
    
      
        
                      
       
        
    
    