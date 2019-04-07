package com.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyuan
 * @version 1.0
 * @description 描述:
 * @date 2019/3/27 11:10
 */
public class Tomcat {
    private int port = 7070;
    private Map<String, String> servletUrl = new HashMap<>();

    public Tomcat() {}

    public Tomcat(int port) {
        this.port = port;
    }

    public void initServletMapping() {
        for (ServletMapping servletMapping : ServletMappingConfig.servletMappingConfig) {
            this.servletUrl.put(servletMapping.getUrl(), servletMapping.getAddress());
        }
    }

    public void dispatch(Socket socket) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Request request = new Request(inputStream);
        String address = this.servletUrl.get(request.getUrl());
        if (address == null){
            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&请求的路径 "+request.getUrl()+" 不存在&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Response response = new Response(outputStream);
        try {
            //反射
            Class servletClass = Class.forName(address);
            Servlet servlet = (Servlet) servletClass.newInstance();
            servlet.service(request, response);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        System.out.println("Tomcat is started ...");
        initServletMapping();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);

            while (true) {
                Socket socket = serverSocket.accept();
                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3,
                        5, 3,
                        TimeUnit.SECONDS, new ArrayBlockingQueue<>(5));
                threadPoolExecutor.execute(() -> dispatch(socket));
            }
//            dispatch(socket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        new Tomcat().start();
    }

}
