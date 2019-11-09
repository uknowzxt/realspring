package com.uknowzxt.context.support;

import com.uknowzxt.core.io.ClassPathResource;
import com.uknowzxt.core.io.Resource;

import java.util.List;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    public ClassPathXmlApplicationContext(String configFile){
        super(configFile);
    }

    //Abstract那里加了双参的构造，这里也应该加上
    public ClassPathXmlApplicationContext(String configFile,ClassLoader cl){
        super(configFile,cl);
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return new ClassPathResource(path,this.getBeanClassLoader() );
    }


}
