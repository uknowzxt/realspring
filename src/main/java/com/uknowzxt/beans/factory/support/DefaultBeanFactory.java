package com.uknowzxt.beans.factory.support;

import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.PropertyValue;
import com.uknowzxt.beans.SimpleTypeConverter;
import com.uknowzxt.beans.factory.BeanCreationException;
import com.uknowzxt.beans.factory.BeanDefinitionStoreException;
import com.uknowzxt.beans.factory.BeanFactory;
import com.uknowzxt.beans.factory.config.BeanPostProcessor;
import com.uknowzxt.beans.factory.config.ConfigurableBeanFactory;
import com.uknowzxt.beans.factory.config.DependencyDescriptor;
import com.uknowzxt.beans.factory.config.InstantiationAwareBeanPostProcessor;
import com.uknowzxt.util.ClassUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory extends DefaultSingletonBeanRegistry implements BeanDefinitionRegistry ,ConfigurableBeanFactory  {

    private final Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
    private ClassLoader beanClassLoader;
    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    public DefaultBeanFactory() {
    }
    public void addBeanPostProcessor(BeanPostProcessor postProcessor){
        this.beanPostProcessors.add(postProcessor);
    }
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanID) {
        return this.beanDefinitionMap.get(beanID);
    }

    @Override
    public void registerBeanDefinition(String beanID, BeanDefinition bd) {
        this.beanDefinitionMap.put(beanID,bd);
    }

    /**
     * 利用反射根据类名创建对象
     * @param beanID
     * @return
     */
    @Override
    public Object getBean(String beanID) {
        BeanDefinition bd = this.getBeanDefinition(beanID);
        if(bd == null){
            throw  new BeanCreationException("Bean Definition does not exist");
        }

        if(bd.isSingleton()){
            Object bean = this.getSingleton(beanID);
            if(bean == null){
                bean = createBean(bd);
                this.registerSingleton(beanID, bean);
            }
            return bean;
        }
        return createBean(bd);
    }

    private Object createBean(BeanDefinition bd) {
        //创建实例
        Object bean = instantiateBean(bd);
        //设置属性
        populateBean(bd, bean);

        return bean;

    }
    private Object instantiateBean(BeanDefinition bd) {//通过bean的className，利用反射的方式，创建出bean的实例
        if(bd.hasConstructorArgumentValues()){//是否有构造参数
            ConstructorResolver resolver = new ConstructorResolver(this);
            return resolver.autowireConstructor(bd);
        }else {
            ClassLoader cl = this.getBeanClassLoader();
            String beanClassName = bd.getBeanClassName();
            try {
                Class<?> clz = cl.loadClass(beanClassName);
                return clz.newInstance();
            } catch (Exception e) {
                throw new BeanCreationException("create bean for " + beanClassName + " failed", e);
            }
        }
    }

    protected void populateBean(BeanDefinition bd, Object bean){
        for(BeanPostProcessor processor : this.getBeanPostProcessors()){
            if(processor instanceof InstantiationAwareBeanPostProcessor){
                ((InstantiationAwareBeanPostProcessor)processor).postProcessPropertyValues(bean, bd.getID());
            }
        }

        List<PropertyValue> pvs = bd.getPropertyValues();//先把bean定义的属性列表拿出来

        if (pvs == null || pvs.isEmpty()) {
            return;
        }

        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this);//转换为实际的类
        SimpleTypeConverter converter = new SimpleTypeConverter();//xml中只能写字符串，纯文本，所以解析类似int类型就会失败，因此有了这个类型转换
        try{

            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());//拿到petStoreService相关信息
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();//拿到属性描述器

            for (PropertyValue pv : pvs){//对bean的属性列表进行遍历
                String propertyName = pv.getName(); //属性的名称
                Object originalValue = pv.getValue();//RuntimeBeanReference/TypedStringValue
                Object resolvedValue = valueResolver.resolveValueIfNecessary(originalValue);//获取属性对象或者值

                //对petStoreService的已经有的字段进行遍历
                //下面for循环可以直接用BeanUtils工具类代替,无论类型如何，能转换就自动帮忙转换
                //BeanUtils.setProperty(bean,propertyName,resolvedValue);

                for (PropertyDescriptor pd : pds) {
                    if(pd.getName().equals(propertyName)){//如果字段名等于得到的属性名。设置属性
                        Object convertedValue = converter.convertIfNecessary(resolvedValue, pd.getPropertyType());//把值和需要的类型传递过去
                        pd.getWriteMethod().invoke(bean, convertedValue);//getWriteMethod就是调用set方法。利用反射，把类和值都设置进来（实例对象，值）
                        break;
                    }
                }

            }
        }catch(Exception ex){
            throw new BeanCreationException("Failed to obtain BeanInfo for class [" + bd.getBeanClassName() + "]", ex);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
          this.beanClassLoader = beanClassLoader;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return (this.beanClassLoader !=null?this.beanClassLoader:ClassUtils.getDefaultClassLoader());
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor) {
        Class<?> typeToMatch = descriptor.getDependencyType();//拿到字段对应的class类型
        for(BeanDefinition bd: this.beanDefinitionMap.values()){
            //确保BeanDefinition 有Class对象
            resolveBeanClass(bd);
            Class<?> beanClass = bd.getBeanClass();//拿到该类定义的class类型
            if(typeToMatch.isAssignableFrom(beanClass)){//判断字段类型与class类型是否相符
                return this.getBean(bd.getID());//如果相符,调用factory的getBean方法,获取到实体类
            }
        }
        return null;
    }

    public void resolveBeanClass(BeanDefinition bd) {
        if(bd.hasBeanClass()){
            return;
        } else{
            try {
                bd.resolveBeanClass(this.getBeanClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("can't load class:"+bd.getBeanClassName());
            }
        }
    }
}
