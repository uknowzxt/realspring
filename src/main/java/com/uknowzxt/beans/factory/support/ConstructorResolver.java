package com.uknowzxt.beans.factory.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.uknowzxt.beans.BeanDefinition;
import com.uknowzxt.beans.ConstructorArgument;
import com.uknowzxt.beans.SimpleTypeConverter;
import com.uknowzxt.beans.factory.BeanCreationException;
import com.uknowzxt.beans.factory.config.ConfigurableBeanFactory;

import java.lang.reflect.Constructor;
import java.util.List;


public class ConstructorResolver {

	protected final Log logger = LogFactory.getLog(getClass());
	
	
	private final ConfigurableBeanFactory beanFactory;


	
	public ConstructorResolver(ConfigurableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	
	public Object autowireConstructor(final BeanDefinition bd) {
		
		Constructor<?> constructorToUse = null;		
		Object[] argsToUse = null;
	
		Class<?> beanClass = null;
		try {
			beanClass = this.beanFactory.getBeanClassLoader().loadClass(bd.getBeanClassName());
			
		} catch (ClassNotFoundException e) {
			throw new BeanCreationException( bd.getID(), "Instantiation of bean failed, can't resolve class", e);
		}	
		
		//获取类的构造方法列表
		Constructor<?>[] candidates = beanClass.getConstructors();	
		
		
		BeanDefinitionValueResolver valueResolver =
				new BeanDefinitionValueResolver(this.beanFactory);

		//获得存在beandefinition中的构造参数列表
		ConstructorArgument cargs = bd.getConstructorArgument();
		SimpleTypeConverter typeConverter = new SimpleTypeConverter();

		//遍历类的构造方法列表
		for(int i=0; i<candidates.length;i++){

			//获取当前构造方法的参数的class
			Class<?> [] parameterTypes = candidates[i].getParameterTypes();
			//当前构造方法的参数的数量与存在beandefinition中的构造参数列表数量不符的出局
			if(parameterTypes.length != cargs.getArgumentCount()){
				continue;
			}
			//参数列表
			argsToUse = new Object[parameterTypes.length];

			//存在beandefinition中的构造参数列表的每个参数是否与当前构造方法的参数一一对应
			boolean result = this.valuesMatchTypes(parameterTypes, 
					cargs.getArgumentValues(), 
					argsToUse, 
					valueResolver, 
					typeConverter);

			//对应的话取出当前构造方法备用
			if(result){
				constructorToUse = candidates[i];
				break;
			}
			
		}
		
		
		//找不到一个合适的构造函数
		if(constructorToUse == null){
			throw new BeanCreationException( bd.getID(), "can't find a apporiate constructor");
		}
		
		
		try {
			return constructorToUse.newInstance(argsToUse);
		} catch (Exception e) {
			throw new BeanCreationException( bd.getID(), "can't find a create instance using "+constructorToUse);
		}		
		
		
	}

	///存在beandefinition中的构造参数列表的每个参数是否与当前构造方法的参数一一对应
	private boolean valuesMatchTypes(Class<?> [] parameterTypes,
			List<ConstructorArgument.ValueHolder> valueHolders,
			Object[] argsToUse,
			BeanDefinitionValueResolver valueResolver,
			SimpleTypeConverter typeConverter ){
		
		//遍历当前构造方法的参数类型
		for(int i=0;i<parameterTypes.length;i++){
			//获取存在beandefinition中的构造参数列表中第i个位置的参数
			ConstructorArgument.ValueHolder valueHolder 
				= valueHolders.get(i);
			//获取参数的值，可能是TypedStringValue, 也可能是RuntimeBeanReference
			Object originalValue = valueHolder.getValue();
			
			try{
				//获得真正的值
				Object resolvedValue = valueResolver.resolveValueIfNecessary( originalValue);
				//如果参数类型是 int, 但是值是字符串,例如"3",还需要转型
				//如果转型失败，则抛出异常。说明这个构造函数不可用
				Object convertedValue = typeConverter.convertIfNecessary(resolvedValue, parameterTypes[i]);
				//转型成功，记录下来
				argsToUse[i] = convertedValue;
			}catch(Exception e){
				logger.error(e);
				return false;
			}				
		}
		return true;
	}
	

}
