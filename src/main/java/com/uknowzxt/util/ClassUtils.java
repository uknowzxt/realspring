package com.uknowzxt.util;

public abstract class ClassUtils {
    //用来装载类的。
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            //首先检查当前线程所用的类加载器
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            //如果当前线程没有类加载器，使用加载ClassUtils的类加载器
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    //如果ClassUtils的类加载器为空，使用系统类加载器
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    //如果这都出错，那就没办法，只能返回null出去，也许调用者能处理呢
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }
}
