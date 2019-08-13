package com.client;

import com.tomcat.request.MyAnnotation;
import com.tomcat.request.Request;
import com.tomcat.request.Response;
import com.tomcat.request.Servlet;

/**
 * @author wuyuan
 * @version 1.0
 * @date 2019/3/27 12:07
 */
@MyAnnotation(url = "/wuyuan2")
public class Wuyuan2Servlet implements Servlet {
    
    @Override
    public void init() {
        System.out.println("wuyuan2Servlet 的 init()方法执行了...");
    }
    
    @Override
    public void doGet(Request request, Response response) {
        response.write("wuyuan2");
    }

    @Override
    public void doPost(Request request, Response response) {
        this.doGet(request, response);
    }
}
