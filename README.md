# realspring

@compenent注解自动注入

##一、目的：
在getBean的过程中, 把bean中带@Autowired注解的字段同时初始化出来, 设置到字段中

*** 

##二、要点

###1.DependencyDescriptor  

    目的: 抽象
    因为@Autowired注解要使用的位置很对, 所以对需要转化的类型做一个抽象, 成员变量
    private  Field field;
    private boolean required;
   
###2.对BeanFactory体系进行扩展
    目的: 通过字段得到实例
    1.要根据DependencyDescriptor解析出实体类,会利用到BeanFactory的getBean()方法,那么要在factory中加入resolveDependency方法,所以对BeanFactory体系进行扩展
    2.在之后进行populateBean的时候, 会对BeanPostProcessor进行遍历, 用到了addBeanPostProcessor(BeanPostProcessor postProcessor)方法和getBeanPostProcessors() 方法. 
    
    (1)更改继承体系
    
              BeanFactory[getBean]         
                👆继承
              AutowireCapableBeanFactory[resolveDependency]
                👆继承
              ConfigurableBeanFactory[ 1.void setBeanClassLoader(ClassLoader beanClassLoader);
                                       2. ClassLoader getBeanClassLoader();	
                                       3. void addBeanPostProcessor(BeanPostProcessor postProcessor);
                                       4. List<BeanPostProcessor> getBeanPostProcessors();] 
                👆实现
              DefaultBeanFactory[ 1. resolveDependency(DependencyDescriptor descriptor)
                                  2. resolveBeanClass(BeanDefinition bd)] --- 暂时只涉及这个,BeanPostProcessor相关在之后处理
              
    (2)在DefaultBeanFactory增加resolveDependency方法
              目的: 遍历beanDefinitionMap,直到找到一个与DependencyDescriptor中feild成员变量class类型相同的类定义,利用这个类定义的BeanId,创建一个实体类
              [1]从传入的DependencyDescriptor获取字段的Class类型
              [2]遍历beanDefinitionMap,拿到每一个BeanDefinition,先调用resolveBeanClass(bd)方法,确保这个类定义中有Class对象. 然后判断这个类定义的Class对象是不是与Feild字段的Class对象相同
              [3]直到找到相同的, 利用factory的getBean方法, 获得一个实体类.
        

###3.InjectionMetadata

    注释: InjectionMetadata调用InjectionElement
    把成员变量List<InjectionElement>代表的每一个feild,利用DependencyDescriptor和factory的resolveDependency()方法解析出实体类, 并赋值给这个对象
   
   1.InjectionElement
    
    (1)目的:调用2中内容,把得到的实例设置进入Feild字段中
    
    (2)继承体系:
                    InjectionMetadata(抽象类)[protected Member member;
                                              protected AutowireCapableBeanFactory factory; 
                                              public abstract void inject(Object target)
                                              ]
                        👆继承
                    AutowiredFieldElement
    
    (3)主要方法:inject(Object target)
                    [1]利用其成员变量,Feild, 创建一个DependencyDescriptor对象.
                    [2]调用factory的resolveDependency(desc)方法, 得到一个该Feild的实例对象.
                    [3]如果实体类存在, 利用反射, 把得到的实例设置进入Feild中.
    
   2.InjectionMetadata
   
    (1)目的:为List<InjectionElement>代表的每一个feild字段赋值
    
    (2) 成员变量: 
                    private final Class<?> targetClass;
                    private List<InjectionElement> injectionElements;
           	
    (3)主要方法:inject(Object target)
                    [1]遍历List<InjectionElement>.拿到每一个InjectionElement 
                    [2]调用InjectionElement的inject方法, 为每一个InjectionElement代表的feid字段赋值. 
           
   
###4.AutowiredAnnotationProcessor

    BeanPostProcessor[  Object beforeInitialization(Object bean, String beanName) throws BeansException;
                      	Object afterInitialization(Object bean, String beanName) throws BeansException;]
        👆继承
    InstantiationAwareBeanPostProcessor[    Object beforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;
                                        	boolean afterInstantiation(Object bean, String beanName) throws BeansException;
                                        	void postProcessPropertyValues(Object bean, String beanName) throws BeansException;]
        👆继承
    AutowiredAnnotationProcessor  [主要方法:
                                       public void postProcessPropertyValues(Object bean, String beanName) throws BeansException
                                       public InjectionMetadata buildAutowiringMetadata(Class<?> clazz)
                                       private Annotation findAutowiredAnnotation(AccessibleObject ao) 
                                   ]
    
     1.目的:拿到目标类的字段, 把其中带有注解的, 转化成AutowiredFieldElement对象集合, 进而转化成InjectionMetadata对象返回
     2.主要方法:postProcessPropertyValues(Object bean, String beanName) 
        (1)调用buildAutowiringMetadata(bean.getClass());创建一个InjectionMetadata
            [1]根据目标类的class类, 获取到类中的所有字段, 遍历这些字段
            [2]利用findAutowiredAnnotation(field)方法, 拿到字段上存在的规定注解
            [3]如果注解存在,并且字段不是static的情况下, 利用feild,factory(成员变量),创建一个AutowiredFieldElement对象, 并把它加入LinkedList<InjectionElement> elements集合中
            [4]利用目标类的class和遍历后得到的LinkedList<InjectionElement> elements, 创建一个InjectionMetadata并返回.
        (2)调用InjectionMetadata的inject(bean)方法, 为bean的字段赋值. 
        

###5.调用时机

    1.目的: 给bean的属性赋值来处理@AutoWired注解再合适不过了. 
    2.时机: 
        (1)修改AbstractApplicationContext的构造方法,在最后调用registerBeanPostProcessors(factory)方法.
                
                public AbstractApplicationContext(String configFile,ClassLoader cl) {
                        factory = new DefaultBeanFactory();
                        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
                        Resource resource =this.getResourceByPath(configFile);
                        reader.loadBeanDefinitions(resource);
                        //todo 这个getBeanClassloader是一个null 这里用了两个构造函数方式
                        //factory.setBeanClassLoader(this.getBeanClassLoader());
                        factory.setBeanClassLoader(cl);
                        registerBeanPostProcessors(factory);
                    }
                    
        (2)AbstractApplicationContext加入registerBeanPostProcessors(factory)方法, 方法中创建AutowiredAnnotationProcessor对象, 并在beanFactory的List<BeanPostProcessor> beanPostProcessors中加入这个AutowiredAnnotationProcessor 
        
                 protected void registerBeanPostProcessors(ConfigurableBeanFactory beanFactory) {
                        AutowiredAnnotationProcessor postProcessor = new AutowiredAnnotationProcessor();
                        postProcessor.setBeanFactory(beanFactory);
                        beanFactory.addBeanPostProcessor(postProcessor);
                    }
                    
        (3)修改populateBean(BeanDefinition bd, Object bean)方法, 在方法的开头, 遍历DefaultBeanFacotry的List<BeanPostProcessor> beanPostProcessors,   并调用每一个BeanPostProcessor的postProcessPropertyValues()方法.
        
                for(BeanPostProcessor processor : this.getBeanPostProcessors()){
                            if(processor instanceof InstantiationAwareBeanPostProcessor){
                                ((InstantiationAwareBeanPostProcessor)processor).postProcessPropertyValues(bean, bd.getID());
                            }
                }

   
                  
 
              
                   
            


    
    
    
        
    
      
        
                      
       
        
    
    