package com.tomcat.server;

import com.tomcat.request.Request;
import com.tomcat.request.Response;
import com.tomcat.request.Servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyuan
 * @version 1.0
 * @date 2019/3/27
 */
public class Tomcat {
    
    //默认端口号
    private int port = 8088;
    
    //存放请求地址和servlet的Class对象的的映射
    private final Map<String, Class> servletUrl = new HashMap<>();
    
    //存放已创建的servlet，确保每个servlet只创建一个
    private final Map<String, Servlet> servletMap = new HashMap<>();
    
    public Tomcat() {
    }
    
    public Tomcat(int port) {
        this.port = port;
    }
    
    private void initServletMapping() {
        ServletMappingConfig.servletMappingConfig.forEach(s -> this.servletUrl.put(s.getUrl(), s.getClazz()));
    }
    
    private Servlet initServlet(String address) {
        Servlet servlet;
        //如果处理该请求的servlet还没有被创建则创建它，否则直接从servletMap中获取
        if ((servlet = this.servletMap.get(address)) == null) {
            try {
                //加锁防止有多个线程同时创建同一个servlet
                synchronized (this) {
                    //双重if判断 -- 可参考懒汉式单例模式的实现(也是双重if判断)
                    if ((servlet = this.servletMap.get(address)) == null) {
                        //创建servlet,此处主动使用该类，所以类会被初始化
                        Class servletClass = this.servletUrl.get(address);
                        
                        //1.直接使用Class对象的newInstance方法实例化对象，只能使用默认的公共无参构造方法
                        servlet = (Servlet) servletClass.newInstance();
                        
                        //2.先使用Class对象获取到指定的Constructor构造方法对象，再用构造方法对象实例化对象，这可以使用所有的构造方法
                        // Constructor constructor = servletClass.getDeclaredConstructor();
                        // constructor.setAccessible(true);
                        // servlet = (Servlet) constructor.newInstance();
                        
                        
                        //调用servlet的init()方法 -- 只在servlet被创建时调用一次
                        servlet.init();
                        //将创建的servlet添加到servletMap中
                        this.servletMap.put(address, servlet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return servlet;
    }
    
    private void dispatch(Socket socket) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            //获取客户端连接的输入输出流
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建request对象，在里面获取http请求信息
        Request request = new Request(inputStream);
        Class clazz = this.servletUrl.get(request.getUrl());
        if (clazz == null) {
            System.err.println("------- 请求的路径 " + request.getUrl() + " 不存在 -------");
            return;
        }
        //创建response对象
        Response response = new Response(outputStream);
        //获取处理该请求的servlet对象
        Servlet servlet = initServlet(request.getUrl());
        //调用servlet的service方法处理请求
        servlet.service(request, response);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void start() {
        initServletMapping();
        ServerSocket serverSocket = null;
        ThreadPoolExecutor threadPoolExecutor = null;
        try {
            serverSocket = new ServerSocket(this.port);
            threadPoolExecutor = new ThreadPoolExecutor(3, 5, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));
            synchronized (this) {
                System.out.println("---------------------");
                System.err.println("Tomcat is started ...\r\n");
            }
            while (true) {
                //获取客户端连接，获取一个就交给一个线程处理，主线程继续监听端口，获取新连接
                Socket socket = serverSocket.accept();
                threadPoolExecutor.execute(() -> dispatch(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
                threadPoolExecutor.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void run(){
        new Tomcat().start();
    }
}
