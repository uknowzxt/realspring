# realspring

@compenent注解自动注入

##一、目的：

在配置文件中配置<context:component-scan base-package="com.uknowzxt.service.v4,com.uknowzxt.dao.v4"/>, 自动扫描配置的所有的包下面的所有类, 把带有@Compenent的注解的类, 生成beanDefinition, 放入BeanFactory. 
*** 

##二、要点

###1.PackageResourceLoader  
    注释: 把包里面的所有类变成Resource类型  
    (1) 把传入的包名, "."全部变成"/";  
    (2) 利用classloader的getResource()方法,获取文件在磁盘中的真实路径, new File()获取到包名文件夹作为进行判断的根文件夹  
    (3) 获取该包名的代表的文件夹下面的所有文件, 遍历这些文件.   
    如果文件不是一个文件夹的话, 把文件加入set<File>集合中.如果文件是一个文件夹的话, 递归调用本步骤,   
    最终达到,把根目录下的所有文件都放入set<File>中的目的.  
    (4) 创建Resource[]数组, 大小和set<File>长度一致, 将set集合中的file文件全部转换成resource文件, 放入resource数组  
   
###2.ASM部分
    类的元数据类: ClassMetaData类的信息元数据类 AnnotationMetadata类注解信息元数据类
    ASM运用类:
    (1)类的信息部分
        
              ClassVisitor(ASM抽象类)         
                👆继承
              ClassMetaDataReading
                👆继承
              AnnotationMetadataReadingVisitor  
    (2)注解的信息部分
   
               AnnotationVisitor(ASM抽象类)
                 👆继承
               AnnotationAttributeReadingVisitor
              
    最终类图展示:

              ClassVisitor         
                👆继承
              ClassMetaDataReading   👉实现:   ClassMetaData
                👆继承
              AnnotationMetadataReadingVisitor   👉实现:   ClassMetaData
                👆被调用
              AnnotationAttributeReadingVisitor   👉继承 :  AnnotationVisitor
              
       (1)ClassMetaDataReading因为继承了ClassVisitor类, 重写visit()方法, 在访问类的时候, 会去访问visit(), 可以获取类的信息.    
       (2)AnnotationMetadataReadingVisitor因为继承了ClassMetaDataReading, 重写visitAnnotation()方法, 所以可以获取注解相关信息, 放入set集合中. 调用 new AnnotaionAttributeReadingVisitor().  
       (3)AnnotationAttributeReadingVisitor因为继承了AnnotationVisitor, 重写了visit()和visitEnd()方法, 在visit()方法中获取注解相关的属性名和属性值. 放入 AnnotationAttributes( extends LinkedHashMap<属性名, 属性值>)中, 在visitEnd()方法中, 把注解类名,及Map<属性名,属性值>加入到一个map集合中.   
       最终达到:通过一个InputStream流,获取到该流代表的类的信息, 注解信息. 
        
        ClassPathResource resource = new ClassPathResource("org/litespring/service/v4/PetStoreService.class");
        ClassReader reader = new ClassReader(resource.getInputStream());
        AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor();
        reader.accept(visitor, ClassReader.SKIP_DEBUG);
        

###3.对ASM部分(2)进行封装

    MetadataReader
        👆实现
    SimpleMetadataReader
    
   对ASM进行封装, 根据Resource文件, 进行ASM, 对成员变量Resource/ClassMetadata/AnnotationMetadata 进行赋值
   ```` java
   	private final Resource resource;
   
   	private final ClassMetadata classMetadata;
   
   	private final AnnotationMetadata annotationMetadata;
   
   
   	public SimpleMetadataReader(Resource resource) throws IOException {
   		InputStream is = new BufferedInputStream(resource.getInputStream());
   		ClassReader classReader;
   		
   		try {
   			classReader = new ClassReader(is);
   		}
   		finally {
   			is.close();
   		}
   
   		AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor();
   		classReader.accept(visitor, ClassReader.SKIP_DEBUG);
   
   		this.annotationMetadata = visitor;
   		this.classMetadata = visitor;
   		this.resource = resource;
   	}
   ````
   
###4.对类定义实体类进行改造

    BeanDefinition
        👆继承
    GenericBeanDefinition
        👆继承
    ScannedGenericBeanDefinition  👉实现  AnnotatedBeanDefinition(扩展接口,有获取类的元数据[包括注解]的功能)
    
因为类定义中要保有注解相关信息, 为了少耦合, 让新的类定义类扩展AnnotatedBeanDefinition接口.

###5.ClassPathBeanDefinitionScanner  
    注释: 把PackageResourceLoader(1)实现的内容和SimpleMetadataReader(3)实现的内容进行分装  
    (1)把拿到的包名们字符串通过","进行拆分  
    (2)遍历拿到的包名字符串  
    (3)遍历每一个包名,把包名下所有涉及到的类转化成BeanDefinition, 放入set集合返回  
       [1]利用PackageResourceLoader类获得该包名下的所有的类的Resource[]数组  
       [2]遍历Resource[]数组, 利用SimpleMetadataReader(3), 把Resource转换成类的元注解(AnnotationMetadata), 并放入利用SimpleMetadataReader成员变量中.   
       [3]如果类上存在@Compenent注解, 创建ScannedGenericBeanDefinition, 传入AnnotationMetadata, 从传入AnnotationMetadata中得到className, 放入BeanDefinition的beanClassName字段中.  
       [4]获取beanId, 找到注解的value属性, 如果存在属性值,即使用value的属性值作为beanId, 否则使用类名小写  
       [5]为ScannedGenericBeanDefinition补全Id.
       [6]把获取的完整ScannedGenericBeanDefinition放入set集合返回  
    (4)把包名中得到的Set<BeanDefinition>进行遍历, 逐一注册进入DefaultBeanFactory的beanDefinitionMap( Map<String, BeanDefinition> )中      

###6.  XmlBeanDefinitionReader  
    注释: 本次改动主要目的在于, 从xml文件读取信息的时候, 区分< context:component-scan >标签, 并需要对包进行扫描, 注册该包名下的类. 
    (1)读取xml文件判断标签所属URI,如果是http://www.springframework.org/schema/context需要把解析出来的包名下的类进行扫描
    (2)调用ClassPathBeanDefinitionScanner(5)doScan()方法, 扫描包名中的所有类, 注册到factory中.
    
   
                  
 
              
                   
            
               


*** 

##三、答疑要点

    
    
    
        
    
      
        
                      
       
        
    
    