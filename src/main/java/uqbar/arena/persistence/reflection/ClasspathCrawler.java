package uqbar.arena.persistence.reflection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

public class ClasspathCrawler {
	private ClassLoader classLoader = null;
	private List<String> classPaths = new ArrayList<String>();
	private String packageName;
	private Logger log = Logger.getLogger(this.getClass());
	
	public ClasspathCrawler(ClassLoader classLoader) {
		super();
		this.classLoader = classLoader;
		this.classPaths.addAll(Arrays.asList(System.getProperty("java.class.path").split(File.pathSeparatorChar + "")));
		
		if(classLoader instanceof URLClassLoader){
			this.classPaths.addAll(getClasspathFromURLClassLoader((URLClassLoader)classLoader));
		}
		
	}

	private List<String> getClasspathFromURLClassLoader(URLClassLoader classLoader) {
		List<String> result = new ArrayList<String>();
		
		for(URL u : classLoader.getURLs()){
			String s = u.toString();
			if(s.startsWith("file:"))
				s = s.substring(5);
			result.add(s);
		}
		
		return result;
	}

	public Set<Class<?>> getClasses(List<String> packagesName) throws Exception{
	    Set<Class<?>> set = new HashSet<Class<?>>();
		for(String pkg:packagesName){
			set.addAll(this.getClasses(pkg));
		}
		return set;
	}
	
	public Set<Class<?>> getClasses(){
		return getClasses("");
	}
	
	@SuppressWarnings("unchecked")
	public Set<Class<?>> getClasses(String packageName){
		this.packageName = packageName;
		
		try{
		    Set<File> set = new HashSet<File>();
		    List<URL> urls;
		    urls = EnumerationUtils.toList(this.classLoader.getResources(this.packageName));
		    
		    if(this.classLoader instanceof URLClassLoader && !this.packageName.equals(""))
		    	urls.addAll(Arrays.asList(((URLClassLoader)classLoader).getURLs())); 
			
			for(URL url : urls){
		        File dir = new File(url.getFile().replace("%20"," "));
		        set.add(dir);
		    }
		    return crawl(set);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	protected Set<Class<?>> crawl(Set<File> set) throws Exception {
		Set<Class<?>> result = new HashSet<Class<?>>();

		for(File f:set){
			if(!f.exists())
				result.addAll(crawlJar(f));
			else
				result.addAll(crawlFiles(f));
		}
		
		return result;
	}
	
	protected Set<Class<?>> crawlFiles(File root) throws Exception {
		Set<Class<?>> result = new HashSet<Class<?>>();

		if(root.isFile()){
			if(isClass(root.getPath()))
				handleFile(root, result);
			if(root.getPath().endsWith(".jar"))
				result.addAll(doCrawlJar(root.getPath()));
		}else{
			for(File f:root.listFiles()){
				result.addAll(this.crawlFiles(f));
			}
		}
		
		return result;
	}

	private void handleFile(File root, Set<Class<?>> result) throws ClassNotFoundException {
		try{
			String name = this.getClassNameFromFile(root.getPath());
			
			if(name.endsWith("$")){
				log.warn("Ignoring class:" + name);
				return;
			}
			
			if(name.startsWith(packageName))
				result.add(Class.forName(name));
		}catch(CouldNotDeduceClassnameException e){
			log.error(e.getMessage());
		}
	}
	

	protected String getClassNameFromFile(String path){
		for(String cp:this.classPaths){
			if(path.startsWith(cp)){
				return getClassName(path.substring(cp.length()));
			}
		}
			
		throw new CouldNotDeduceClassnameException("Could not deduce className for:" + path);
	}

	protected Set<Class<?>> crawlJar(File f) throws Exception {
		String path = f.getPath().split("!")[0].substring(5);
		return doCrawlJar(path);
	}

	private Set<Class<?>> doCrawlJar(String path) throws IOException,
			ClassNotFoundException {
		JarFile jarFile = null;
		Set<Class<?>> result = new HashSet<Class<?>>();

		try{
			jarFile = new JarFile(path);
			Enumeration<JarEntry> entries = jarFile.entries();
			while(entries.hasMoreElements()){
				String entry = entries.nextElement().getName();
				if(isClass(entry)){
					handleJarEntry(result, entry);
				}
			}
		}catch(ZipException e){
			LogFactory.getLog(this.getClass()).warn("Error leyendo el jar: " + path + " se ignora y se continua.", e);
		}finally{
			if(jarFile != null)
				jarFile.close();
		}
		
		return result;
	}

	private void handleJarEntry(Set<Class<?>> result, String entry){
		try{
			String className = this.getClassName(entry);
			
			if(className.startsWith(this.packageName))
				result.add(Class.forName(className));
		}catch(Throwable e){
			log.warn("Ignoring:" + this.getClassName(entry) + ":" + e.getMessage());
		}
	}

	private boolean isClass(String entry) {
		return entry.endsWith(".class");
	}

	protected String getClassName(String path){
		String name = path;
		
		if(name.startsWith("/") || name.startsWith("\\"))
			name = name.substring(1);

		name = name.split(".class")[0].replace("/", ".").replace("\\",".");
		
		return name;
	}
	
}
