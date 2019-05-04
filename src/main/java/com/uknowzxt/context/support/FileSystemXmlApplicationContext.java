package com.uknowzxt.context.support;

import com.uknowzxt.core.io.FileSystemResource;
import com.uknowzxt.core.io.Resource;

public class FileSystemXmlApplicationContext extends AbstractApplicationContext {

    public FileSystemXmlApplicationContext(String configFile) {
        super(configFile);
    }


    @Override
    protected Resource getResourceByPath(String path) {
        return new FileSystemResource(path);
    }


}
