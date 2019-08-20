package com.uknowzxt.core.io.support;

import com.uknowzxt.core.io.FileSystemResource;
import com.uknowzxt.core.io.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.uknowzxt.util.Assert;
import com.uknowzxt.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 把包里面所有类变成Resource(一个resource数组)
 */
public class PackageResourceLoader {

	private static final Log logger = LogFactory.getLog(PackageResourceLoader.class);

	private final ClassLoader classLoader;

	public PackageResourceLoader() {
		this.classLoader = ClassUtils.getDefaultClassLoader();
	}

	public PackageResourceLoader(ClassLoader classLoader) {
		Assert.notNull(classLoader, "ResourceLoader must not be null");
		this.classLoader = classLoader;
	}



	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	public Resource[] getResources(String basePackage) throws IOException {//把传递进来的包名下的所有类变成resource
		Assert.notNull(basePackage, "basePackage  must not be null");
		String location = ClassUtils.convertClassNameToResourcePath(basePackage);//把路径中的点变成了斜杠
		ClassLoader cl = getClassLoader();
		URL url = cl.getResource(location);//获取真实路径
		File rootDir = new File(url.getFile());//当前目录是这个包的根目录
		
		Set<File> matchingFiles = retrieveMatchingFiles(rootDir);//获得了目录下的所有文件的列表
		Resource[] result = new Resource[matchingFiles.size()];//一个Resource数组
		int i=0;
		for (File file : matchingFiles) {
			result[i++]=new FileSystemResource(file);
		}
		return result;//返回这个resource数组
		
	}
	protected Set<File> retrieveMatchingFiles(File rootDir) throws IOException {
		if (!rootDir.exists()) {//是否存在
			// Silently skip non-existing directories.
			if (logger.isDebugEnabled()) {
				logger.debug("Skipping [" + rootDir.getAbsolutePath() + "] because it does not exist");
			}
			return Collections.emptySet();
		}
		if (!rootDir.isDirectory()) {//是否是文件夹
			// Complain louder if it exists but is no directory.
			if (logger.isWarnEnabled()) {
				logger.warn("Skipping [" + rootDir.getAbsolutePath() + "] because it does not denote a directory");
			}
			return Collections.emptySet();
		}
		if (!rootDir.canRead()) {//是否可读
			if (logger.isWarnEnabled()) {
				logger.warn("Cannot search for matching files underneath directory [" + rootDir.getAbsolutePath() +
						"] because the application is not allowed to read the directory");
			}
			return Collections.emptySet();
		}
		/*String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
		if (!pattern.startsWith("/")) {
			fullPattern += "/";
		}
		fullPattern = fullPattern + StringUtils.replace(pattern, File.separator, "/");
		*/
		Set<File> result = new LinkedHashSet<File>(8);
		doRetrieveMatchingFiles(rootDir, result);
		return result;
	}

	
	protected void doRetrieveMatchingFiles( File dir, Set<File> result) throws IOException {
		
		File[] dirContents = dir.listFiles();
		if (dirContents == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("Could not retrieve contents of directory [" + dir.getAbsolutePath() + "]");
			}
			return;
		}
		for (File content : dirContents) {
			
			if (content.isDirectory() ) {//是否是文件夹
				if (!content.canRead()) {//是否可读,不可读的话跳过这个文件夹
					if (logger.isDebugEnabled()) {
						logger.debug("Skipping subdirectory [" + dir.getAbsolutePath() +
								"] because the application is not allowed to read the directory");
					}
				}
				else {//可读,把这个文件夹用递归的方式继续调用本方法,直到出现文件
					doRetrieveMatchingFiles(content, result);
				}
			} else{//不是文件夹,是一个文件,把文件加入到文件列表中
				result.add(content);
			}
			
		}
	}

}
