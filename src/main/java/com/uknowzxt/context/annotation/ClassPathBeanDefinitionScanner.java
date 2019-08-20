package com.uknowzxt.context.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.factory.BeanDefinitionStoreException;
import com.uknowzxt.beans.factory.support.BeanDefinitionRegistry;
import com.uknowzxt.beans.factory.support.BeanNameGenerator;
import com.uknowzxt.core.io.Resource;
import com.uknowzxt.core.io.support.PackageResourceLoader;
import com.uknowzxt.core.type.classreading.MetadataReader;
import com.uknowzxt.core.type.classreading.SimpleMetadataReader;
import com.uknowzxt.stereotype.Component;
import com.uknowzxt.util.StringUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClassPathBeanDefinitionScanner {
	

	private final BeanDefinitionRegistry registry;//为factory补全beandefinition集合(扫描到的@component注解)
	
	private PackageResourceLoader resourceLoader = new PackageResourceLoader();
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();//用来给扫描到的类, 创建id值 - 类名或者

	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {		
		this.registry = registry;		
	}
	
	public Set<BeanDefinition> doScan(String packagesToScan) {
		
		String[] basePackages = StringUtils.tokenizeToStringArray(packagesToScan,",");
		
		Set<BeanDefinition> beanDefinitions = new LinkedHashSet<BeanDefinition>();
		for (String basePackage : basePackages) {//对每一个包名进行遍历
			Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
			for (BeanDefinition candidate : candidates) {
				beanDefinitions.add(candidate);
				registry.registerBeanDefinition(candidate.getID(),candidate);//获得的beandefinition注册到factory中
				
			}
		}
		return beanDefinitions;
	}
	
	
	
	public Set<BeanDefinition> findCandidateComponents(String basePackage) {
		Set<BeanDefinition> candidates = new LinkedHashSet<BeanDefinition>();
		try {
			
			Resource[] resources = this.resourceLoader.getResources(basePackage);//把该包名下的所有文件变为Resource
			
			for (Resource resource : resources) {//遍历Resource
				try {

					MetadataReader metadataReader = new SimpleMetadataReader(resource);//获取这个resource的类的信息
				
					if(metadataReader.getAnnotationMetadata().hasAnnotation(Component.class.getName())){
						ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader.getAnnotationMetadata());//获得beanDefinition(这里面有所不同是因为,要把注解相关的类信息也放入类定义里面)
						String beanName = this.beanNameGenerator.generateBeanName(sbd, this.registry);//要把类名字确定一下,注解的value值,或者是类名的小写
						sbd.setId(beanName);
						candidates.add(sbd);					
					}
				}
				catch (Throwable ex) {
					throw new BeanDefinitionStoreException(
							"Failed to read candidate component class: " + resource, ex);
				}
				
			}
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return candidates;
	}
	
}
