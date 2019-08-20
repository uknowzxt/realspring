package com.uknowzxt.beans.factory.xml;

import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.ConstructorArgument;
import com.uknowzxt.beans.PropertyValue;
import com.uknowzxt.beans.factory.BeanDefinitionStoreException;
import com.uknowzxt.beans.factory.config.RuntimeBeanReference;
import com.uknowzxt.beans.factory.config.TypedStringValue;
import com.uknowzxt.beans.factory.support.BeanDefinitionRegistry;
import com.uknowzxt.beans.factory.support.GenericBeanDefinition;
import com.uknowzxt.context.annotation.ClassPathBeanDefinitionScanner;
import com.uknowzxt.core.io.Resource;
import com.uknowzxt.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class XmlBeanDefinitionReader {
    public static final String ID_ATTRIBUTE = "id";

    public static final String CLASS_ATTRIBUTE = "class";

    public static final String SCOPE_ATTRIBUTE = "scope";

    public static final String PROPERTY_ELEMENT = "property";

    public static final String REF_ATTRIBUTE = "ref";

    public static final String VALUE_ATTRIBUTE = "value";

    public static final String NAME_ATTRIBUTE = "name";

    public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";

    public static final String TYPE_ATTRIBUTE = "type";

    public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";

    public static final String CONTEXT_NAMESPACE_URI = "http://www.springframework.org/schema/context";

    private static final String BASE_PACKAGE_ATTRIBUTE = "base-package";

    BeanDefinitionRegistry registry;


    protected final Log logger = LogFactory.getLog(getClass());


    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry){
        this.registry = registry;
    }

    public void loadBeanDefinitions(Resource resource){
        InputStream is = null;
        try{
            is = resource.getInputStream();
            SAXReader reader = new SAXReader();
            Document doc = reader.read(is);

            //解析xml
            Element root = doc.getRootElement(); //<beans>
            Iterator<Element> iter = root.elementIterator();
            while(iter.hasNext()){
                Element ele = (Element)iter.next();
                String namespaceUri = ele.getNamespaceURI();
                if(this.isDefaultNamespace(namespaceUri)){//http://www.springframework.org/schema/beans
                    parseDefaultElement(ele); //普通的bean
                } else if(this.isContextNamespace(namespaceUri)){//http://www.springframework.org/schema/context
                    parseComponentElement(ele); //例如<context:component-scan>
                }
            }
        } catch (Exception e) {
            throw new BeanDefinitionStoreException("IOException parsing XML document from " + resource.getDescription(),e);
        }finally{
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void parseComponentElement(Element ele) {
        String basePackages = ele.attributeValue(BASE_PACKAGE_ATTRIBUTE);//获取base-package属性的值
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        scanner.doScan(basePackages);//把类型传入, 对包名下的类进行扫描.

    }
    private void parseDefaultElement(Element ele) {
        String id = ele.attributeValue(ID_ATTRIBUTE);//获取id属性的值
        String beanClassName = ele.attributeValue(CLASS_ATTRIBUTE);//获取class属性的值
        BeanDefinition bd = new GenericBeanDefinition(id,beanClassName);
        if (ele.attribute(SCOPE_ATTRIBUTE)!=null) {
            bd.setScope(ele.attributeValue(SCOPE_ATTRIBUTE));
        }
        parseConstructorArgElements(ele,bd);//解析构造方法参数, 并把构造方法参数列表放入bd的constructorArgument的List<ValueHolder>中
        parsePropertyElement(ele,bd);//解析属性,并把属性放入bd的list
        this.registry.registerBeanDefinition(id, bd);//为factory的map设置bean定义

    }
    public boolean isDefaultNamespace(String namespaceUri) {
        return (!StringUtils.hasLength(namespaceUri) || BEANS_NAMESPACE_URI.equals(namespaceUri));
    }
    public boolean isContextNamespace(String namespaceUri){
        return (!StringUtils.hasLength(namespaceUri) || CONTEXT_NAMESPACE_URI.equals(namespaceUri));
    }

    public void parseConstructorArgElements(Element beanEle, BeanDefinition bd) {
        Iterator iter = beanEle.elementIterator(CONSTRUCTOR_ARG_ELEMENT);//<<constructor-arg>
        while(iter.hasNext()){
            Element ele = (Element)iter.next();
            parseConstructorArgElement(ele, bd);
        }

    }
    public void parseConstructorArgElement(Element ele, BeanDefinition bd) {

        String typeAttr = ele.attributeValue(TYPE_ATTRIBUTE);
        String nameAttr = ele.attributeValue(NAME_ATTRIBUTE);
        Object value = parsePropertyValue(ele, bd, null);//value是RuntimeReference或者TypeStringValue
        ConstructorArgument.ValueHolder valueHolder = new ConstructorArgument.ValueHolder(value);
        if (StringUtils.hasLength(typeAttr)) {
            valueHolder.setType(typeAttr);
        }
        if (StringUtils.hasLength(nameAttr)) {
            valueHolder.setName(nameAttr);
        }

        bd.getConstructorArgument().addArgumentValue(valueHolder);
    }

    public void parsePropertyElement(Element beanElem, BeanDefinition bd) {
        //对<bean>下面的标签进行遍历——<properties>
        Iterator iter= beanElem.elementIterator(PROPERTY_ELEMENT);
        while(iter.hasNext()){
            Element propElem = (Element)iter.next();//<property>
            String propertyName = propElem.attributeValue(NAME_ATTRIBUTE);//获取property标签的name属性
            if (!StringUtils.hasLength(propertyName)) {//判断name是不是空的，如果是空的，直接返回错误
                logger.fatal("Tag 'property' must have a 'name' attribute");
                return;
            }


            Object val = parsePropertyValue(propElem, bd, propertyName);
            PropertyValue pv = new PropertyValue(propertyName, val);

            bd.getPropertyValues().add(pv);//向beanDefinition中的数组添加数据
        }

    }

    public Object parsePropertyValue(Element ele, BeanDefinition bd, String propertyName) {
        String elementName = (propertyName != null) ?
                "<property> element for property '" + propertyName + "'" :
                "<constructor-arg> element";


        boolean hasRefAttribute = (ele.attribute(REF_ATTRIBUTE)!=null);//当前<property>标签的ref标签是否为空
        boolean hasValueAttribute = (ele.attribute(VALUE_ATTRIBUTE) !=null);//当前<property>标签的value属性是否为空

        if (hasRefAttribute) {//ref属性
            String refName = ele.attributeValue(REF_ATTRIBUTE);//得到ref属性
            if (!StringUtils.hasText(refName)) {
                logger.error(elementName + " contains empty 'ref' attribute");
            }
            RuntimeBeanReference ref = new RuntimeBeanReference(refName);//创建一个RuntimeReference对象，包含beanName属性
            return ref;
        }else if (hasValueAttribute) {//value属性
            TypedStringValue valueHolder = new TypedStringValue(ele.attributeValue(VALUE_ATTRIBUTE));//得到value属性并创建TypedStringValue对象。

            return valueHolder;
        }
        else {
            //都是空的抛出错误
            throw new RuntimeException(elementName + " must specify a ref or value");
        }
    }

}
