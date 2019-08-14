package com.tomcat.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * classloader
 *
 * @author wuyuan
 * @date 2019/7/24
 * @version 1.0
 */
public class MyClassLoader extends ClassLoader {
    
    //定义类加载器的名字
    private String classLoaderName;
    
    //要加载类的磁盘路径
    private String path;
    
    //定义字节码文件结尾名 -- 字节码文件都是以 .class 结尾的
    private final String fileExtension = ".class";
    
    //保存所有当前类加载器加载的类的Class对象
    private final Map<String, Class<?>> loadedClassMap = new HashMap<>();
    
    public MyClassLoader(String classLoaderName) {
        super();
        this.classLoaderName = classLoaderName;
    }
    
    public MyClassLoader(ClassLoader parent, String classLoaderName) {
        super(parent);
        this.classLoaderName = classLoaderName;
    }
    
    /**
     * 重写ClassLoader的 findClass方法，该方法会在 loadClass方法中调用
     * -- 自定义的类加载器必须重写父类的findClass方法，并调用父类的 defineClass来加载类，生成类的Class对象
     *
     * 根据给定的类的名字，创建出类的Class对象 -- 加载类
     */
    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        
        System.err.println(LocalTime.now() + ":类加载器:" + this.classLoaderName + "-加载servlet: " + className);
        
        //获取类的字节码数据
        byte[] data = this.loadClassData(className);
        
        //生成类的Class对象，此时，类就被加载了
        return super.defineClass(className, data, 0, data.length);
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        //首先判断是否已被当前类加载器加载
        Class<?> clazz = findLoadedClass(name);
        if (clazz == null) {
            //交给父类加载器加载
            if (name.endsWith(".tomcat.request.Servlet")
                    || name.endsWith(".tomcat.request.MyAnnotation")
                    || name.endsWith(".tomcat.request.Request")
                    || name.endsWith(".tomcat.request.Response")
                    || name.startsWith("java.")
                    || name.startsWith("sun.")
                    || name.startsWith("javax.")) {
                clazz = super.loadClass(name);
            } else {
                //否则自己加载
                clazz = this.findClass(name);
            }
        }
        return clazz;
    }
    
    //根据类的名字(如com.jvm.classloader.MyClassLoader)，获取到类的字节码数据
    private byte[] loadClassData(String className) {
        InputStream is = null;
        byte[] data = null;
        ByteArrayOutputStream baos = null;
        
        try {
            //把全类名的.替换成/，组合成文件路径的形式
            className = className.replaceAll("\\.", "//");
            is = new FileInputStream(new File(this.path + className + this.fileExtension));
            baos = new ByteArrayOutputStream();
            
            int ch = 0;
            while ((ch = is.read()) != -1) {
                baos.write(ch);
            }
            data = baos.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                is.close();
                baos.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return data;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getClassLoaderName() {
        return classLoaderName;
    }
    
    public void setClassLoaderName(String classLoaderName) {
        this.classLoaderName = classLoaderName;
    }
    
    public Map<String, Class<?>> getLoadedClassMap() {
        return loadedClassMap;
    }
}
