package com.uknowzxt.core.type.classreading;

import com.uknowzxt.core.type.ClassMetadata;
import com.uknowzxt.util.ClassUtils;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.SpringAsmInfo;


/**
 * 获取类的信息(只重写了visit方法)
 */
public class ClassMetadataReadingVisitor extends ClassVisitor implements ClassMetadata {

	private String className;

	private boolean isInterface;

	private boolean isAbstract;

	private boolean isFinal;

	private String superClassName;

	private String[] interfaces;



	public ClassMetadataReadingVisitor() {
		super(SpringAsmInfo.ASM_VERSION);
	}


	/**
	 *
	 * @param version javac在编译class文件的时候,写入了一个版本号
	 * @param access public/private/final/abstract .. 通过二进制的形式,每一位代表一个信息
	 * @param name  className 乐星
	 * @param signature
	 * @param supername 父类
	 * @param interfaces 实现的接口
	 */
	public void visit(int version, int access, String name, String signature, String supername, String[] interfaces) {
		this.className = ClassUtils.convertResourcePathToClassName(name);//把斜杠变成.
		this.isInterface = ((access & Opcodes.ACC_INTERFACE) != 0);
		this.isAbstract = ((access & Opcodes.ACC_ABSTRACT) != 0);
		this.isFinal = ((access & Opcodes.ACC_FINAL) != 0);
		if (supername != null) {//把斜杠变成 .
			this.superClassName = ClassUtils.convertResourcePathToClassName(supername);
		}
		this.interfaces = new String[interfaces.length];
		for (int i = 0; i < interfaces.length; i++) {// 把斜杠变成.
			this.interfaces[i] = ClassUtils.convertResourcePathToClassName(interfaces[i]);
		}
	}


	public String getClassName() {
		return this.className;
	}

	public boolean isInterface() {
		return this.isInterface;
	}

	public boolean isAbstract() {
		return this.isAbstract;
	}

	public boolean isConcrete() {
		return !(this.isInterface || this.isAbstract);
	}

	public boolean isFinal() {
		return this.isFinal;
	}


	public boolean hasSuperClass() {
		return (this.superClassName != null);
	}

	public String getSuperClassName() {
		return this.superClassName;
	}

	public String[] getInterfaceNames() {
		return this.interfaces;
	}


}
