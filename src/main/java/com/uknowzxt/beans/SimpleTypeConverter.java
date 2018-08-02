package com.uknowzxt.beans;


import com.uknowzxt.beans.propertyeditors.CustomBooleanEditor;
import com.uknowzxt.beans.propertyeditors.CustomNumberEditor;
import com.uknowzxt.util.ClassUtils;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

public class SimpleTypeConverter implements TypeConverter {

	private Map<Class<?>, PropertyEditor> defaultEditors;

	public SimpleTypeConverter(){

	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException {

		//这个值value可不可以直接转成required类型
		if(ClassUtils.isAssignableValue(requiredType, value)){
			//可以直接返回
			return (T)value;
		} else{
			//如果不行，看看值是不是string，现在只支持string
			if(value instanceof String){

				//从 map中取出这个class对应的editor
				PropertyEditor editor = findDefaultEditor(requiredType);
				try{
					editor.setAsText((String)value);//把参数设置进去
				}catch(IllegalArgumentException e){
					throw new TypeMismatchException(value,requiredType);
				}
				return (T)editor.getValue();
			} else{
				throw new RuntimeException("Todo : can't convert value for "+value +" class:"+requiredType);
			}
		}
	}
	private PropertyEditor findDefaultEditor(Class<?> requiredType) {
		PropertyEditor editor = this.getDefaultEditor(requiredType);
		if(editor == null){
			throw new RuntimeException("Editor for "+ requiredType +" has not been implemented");
		}
		return editor;
	}

	public PropertyEditor getDefaultEditor(Class<?> requiredType) {

		//判断属性编辑是不是空，如果是空，创建一个新的
		if (this.defaultEditors == null) {
			createDefaultEditors();
		}
		return this.defaultEditors.get(requiredType);
	}
	
	private void createDefaultEditors() {
		//把class和对应需要使用的editor放入map

		this.defaultEditors = new HashMap<Class<?>, PropertyEditor>(64);

		// Spring's CustomBooleanEditor accepts more flag values than the JDK's default editor.
		this.defaultEditors.put(boolean.class, new CustomBooleanEditor(false));
		this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));

		// The JDK does not contain default editors for number wrapper types!
		// Override JDK primitive number editors with our own CustomNumberEditor.
		/*this.defaultEditors.put(byte.class, new CustomNumberEditor(Byte.class, false));
		this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
		this.defaultEditors.put(short.class, new CustomNumberEditor(Short.class, false));
		this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));*/
		this.defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, false));
		this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
		/*this.defaultEditors.put(long.class, new CustomNumberEditor(Long.class, false));
		this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
		this.defaultEditors.put(float.class, new CustomNumberEditor(Float.class, false));
		this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
		this.defaultEditors.put(double.class, new CustomNumberEditor(Double.class, false));
		this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
		this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));*/

		
	}

}
