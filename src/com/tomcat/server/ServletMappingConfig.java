package com.tomcat.server;

import com.tomcat.Run;
import com.tomcat.classloader.MyClassLoader;
import com.tomcat.request.MyAnnotation;
import com.tomcat.request.Servlet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置哪个url访问哪个servlet
 *
 * @author wuyuan
 * @version 1.0
 * @date 2019/3/27 11:13
 */
class ServletMappingConfig {
    
    //存放所有servlet的请求路径和Class对象的映射
    static List<ServletMapping> servletMappingConfig = new ArrayList<>();
    
    //存放每一个web应用的类加载器
    private static Map<String, ClassLoader> classLoaderMap = new HashMap<>();
    
    //存储tomcat所在文件夹的上级文件夹
    private static ThreadLocal<String> THREADLOCAL = new ThreadLocal<>();
    
    static {
        /*
         * 查找并加载所有和tomcat文件夹的上级文件夹下的所有servlet (包括子文件夹)
         */
        
        //获取tomcat所在文件夹的上级文件夹
        String path = new File(Run.class.getResource("").getPath()).getParent();
        try {
            //使用 utf-8 对路径进行编码
            path = URLDecoder.decode(path, "utf-8");
            //将目录名添加到threadlocal中
            THREADLOCAL.set(path);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //将应用类加载器存到classLoaderMap中,方便后面使用
        classLoaderMap.put(path, ClassLoader.getSystemClassLoader());
        //调用此方法后，所有tomcat上层文件夹中的类都被加载了，但没有被初始化，在有请求时第一次实例化servlet时初始化
        findAndDefineServlet(new File(path));
        //将线程上下文类加载器设置成应用类加载器
        Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
        //删除threadlocal中的数据
        THREADLOCAL.remove();
    }
    
    //递归加载所有servlet
    private static void findAndDefineServlet(File src) {
        //如果是文件，则说明可能是个servlet
        if (src.isFile()) {
            //如果文件不是以.class结尾,则说明该文件不是类,直接忽略跳过
            if (!src.toString().endsWith(".class")) {
                return;
            }
            //获取全类名
            String allClassName = new File(THREADLOCAL.get()).getName() + "." +
                    src.toString().replace(THREADLOCAL.get() + "\\", "")
                            .replace("\\", ".").replace(".class", "");
            try {
                //使用当前的线程上下文类加载器加载servlet -- 这样的话所有的类都会在tomcat启动时被加载，但没有被初始化
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Class servletClass = classLoader.loadClass(allClassName);
                //如果当前类加载器是自定义类加载器,也就是当前类加载器是每一个独立的web应用的类加载器,则将加载到到的Class对象都存到类加载器的map里
                if (classLoader instanceof MyClassLoader) {
                    ((MyClassLoader) classLoader).getLoadedClassMap().put(allClassName, servletClass);
                }
                //如果类实现了Servlet接口并且类上面有 MyAnnotation 注解的话就说明这是一个servlet，否则忽略
                if (Servlet.class.isAssignableFrom(servletClass) && servletClass.isAnnotationPresent(MyAnnotation.class)) {
                    //获取servlet上面的 MyAnnotation 注解
                    MyAnnotation ann = (MyAnnotation) servletClass.getAnnotation(MyAnnotation.class);
                    //获取该servlet的访问路径
                    String url = ann.url().startsWith("/") ? ann.url() : "/" + ann.url();
                    
                    //把servlet的请求路径和Class对象映射存到list中
                    servletMappingConfig.add(new ServletMapping(url, servletClass));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                String path = URLDecoder.decode(src.toString(), "utf-8");
                //如果该文件夹是tomcat的同级文件夹,就认为它是一个独立的web应用,就为它新创建一个类加载器
                if (URLDecoder.decode(src.getParent(), "utf-8").equals(THREADLOCAL.get())) {
                    MyClassLoader classLoader;
                    //如果原来没有,则先创建一个
                    if ((classLoader = (MyClassLoader) classLoaderMap.get(path)) == null) {
                        String f = new File(THREADLOCAL.get()).getParent();
                        classLoader = new MyClassLoader(ClassLoader.getSystemClassLoader(), src.toString().replace(f + "\\", "").replace("\\", "."));
                        classLoader.setPath(f + "\\");
                        classLoaderMap.put(path, classLoader);
                        System.out.println(LocalTime.now() + ":创建类加载器:" + classLoader.getClassLoaderName() + ",加载" + path + "下的所有servlet");
                    }
                    Thread.currentThread().setContextClassLoader(classLoader);
                } else if (path.equals(THREADLOCAL.get())) {
                    //如果是tomcat的上级文件夹中的类,则使用系统类加载器,这里的东西每一个web应用都可以使用
                    Thread.currentThread().setContextClassLoader(classLoaderMap.get(THREADLOCAL.get()));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            
            /*
             * 因为扫描的是被编译过的.class文件而不是.java文件,里面一定有.class文件,所以文件夹
             * 一定不会是空文件夹,因为只有原来文件夹里有.java文件才会被编译生成含有.class文件的文件夹
             *
             * 所以下面这个if条件一定是true
             */
            //获取文件夹下所有的文件和文件夹
            File[] files;
            if ((files = src.listFiles()) != null) {
                for (File file : files) {
                    //排除掉tomcat文件夹 -- 这是tomcat，里面肯定不会有servlet
                    if (!file.toString().endsWith("tomcat")) {
                        findAndDefineServlet(file);
                    }
                }
            }
        }
    }
}
