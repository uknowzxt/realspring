package com.uknowzxt.beans.factory.support;

import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.factory.BeanCreationException;
import com.uknowzxt.beans.factory.BeanDefinitionStoreException;
import com.uknowzxt.beans.factory.BeanFactory;
import com.uknowzxt.util.ClassUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaulBeanFactory implements BeanFactory {
    public static final String ID_ATTRIBUTE = "id";
    public static final String CLASS_ATTRIBUTE = "class";
    private final Map<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    public DefaulBeanFactory(String configFile) {
        loadBeanDefinition(configFile);
    }

    /**
     *用来解析被传入的xml文件，分解出id和 类定义对象（id 和 class名称），放入成员对象map中 。
     */
    private void loadBeanDefinition(String configFile) {
        InputStream is = null;
        try {
            //利用类加载器来加载文件
            ClassLoader cl = ClassUtils.getDefaultClassLoader();
            is = cl.getResourceAsStream(configFile);


            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);
            //dom4j解析
            Element root = doc.getRootElement();//获得根目录 <beans>
            Iterator<Element> iter = root.elementIterator();
            while (iter.hasNext()){
                Element ele = (Element) iter.next();
                String id = ele.attributeValue(ID_ATTRIBUTE);
                String beanClassName = ele.attributeValue(CLASS_ATTRIBUTE);
                BeanDefinition bd = new GenericBeanDefinition(id, beanClassName);
                this.beanDefinitionMap.put(id,bd);//把解析的bean 保留在map中 可以在getBeanDefinition中返回
            }

        }catch (DocumentException e){
            //异常处理
            throw new BeanDefinitionStoreException("IOException parsing XML document",e);
        }finally {
            if(is != null){
                try{
                    is.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanID) {
        return this.beanDefinitionMap.get(beanID);
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
        ClassLoader cl = ClassUtils.getDefaultClassLoader();
        String beanClassName = bd.getBeanClassName();
        try {
            //通过反射获得对象 -- 加载的类 需要有无參构造方法
            Class<?> clz = cl.loadClass(beanClassName);
            return clz.newInstance();
        }catch (Exception e){
            //异常处理
            throw  new BeanCreationException("create bean for" + beanClassName + "failed",e);
        }

    }
}
