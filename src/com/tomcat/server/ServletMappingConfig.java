package com.tomcat.server;

import com.tomcat.request.MyAnnotation;
import com.tomcat.request.Servlet;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
    
    private static ThreadLocal<String> THREADLOCAL = new ThreadLocal<>();
    
    static {
        /*
         * 查找并加载所有和tomcat文件夹的上级文件夹下的所有servlet (包括子文件夹)
         */
        
        //获取tomcat所在文件夹的上级文件夹
        String path = new File(Tomcat.class.getResource("").getPath()).getParent();
        try {
            //使用 utf-8 对路径进行编码
            path = URLDecoder.decode(path, "utf-8");
            //将目录名添加到threadlocal中
            THREADLOCAL.set(path);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //调用此方法后，所有tomcat上层文件夹中的类都被加载了，但没有被初始化，在有请求时第一次实例化servlet时初始化
        findAndDefineServlet(new File(path));
        //删除threadlocal中的数据
        THREADLOCAL.remove();
        
    }
    
    //递归加载所有servlet
    private static void findAndDefineServlet(File src) {
        //如果是文件，则说明可能是个servlet
        if (src.isFile()) {
            //获取全类名
            String allClassName = new File(THREADLOCAL.get()).getName() + "." +
                    src.toString().replace(THREADLOCAL.get() + "\\", "")
                            .replace("\\", ".").replace(".class", "");
            try {
                //使用线程上下文类加载器加载servlet -- 这样的话所有的类都会在tomcat启动时被加载，但没有被初始化
                Class servletClass = Thread.currentThread().getContextClassLoader().loadClass(allClassName);
                //如果类实现了Servlet接口并且类上面有 MyAnnotation 注解的话就说明这是一个servlet，否则忽略
                if (Servlet.class.isAssignableFrom(servletClass) && servletClass.isAnnotationPresent(MyAnnotation.class)) {
                    //获取servlet上面的 MyAnnotation 注解
                    MyAnnotation ann = (MyAnnotation) servletClass.getAnnotation(MyAnnotation.class);
                    //获取该servlet的访问路径
                    String url = ann.url().startsWith("/") ? ann.url() : "/" + ann.url();
                    //获取类名
                    String className = allClassName.substring(allClassName.lastIndexOf("."));
                    //把servlet的请求路径和Class对象映射存到list中
                    servletMappingConfig.add(new ServletMapping(url, servletClass));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            /*
             * 因为扫描的是被编译过的.class文件而不是.java文件,里面一定有.class文件,所以文件夹
             * 一定不会是空文件夹,因为只有原来文件夹里有.java文件才会被编译生成含有.class文件的文件夹
             *
             * 所以这个if条件一定是true
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
