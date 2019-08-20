package com.uknowzxt.test.v4;

import org.junit.Assert;
import org.junit.Test;
import com.uknowzxt.core.io.Resource;
import com.uknowzxt.core.io.support.PackageResourceLoader;

import java.io.IOException;
//1 测试从包名下获取类的resource文件
public class PackageResourceLoaderTest {

	@Test
	public void testGetResources() throws IOException{
		PackageResourceLoader loader = new PackageResourceLoader();
		Resource[] resources = loader.getResources("com.uknowzxt.dao.v4");
		Assert.assertEquals(2, resources.length);
	}

}
