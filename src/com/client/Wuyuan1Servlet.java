package com.client;

import com.tomcat.request.MyAnnotation;
import com.tomcat.request.Request;
import com.tomcat.request.Response;
import com.tomcat.request.Servlet;

/**
 * @author wuyuan
 * @date 2019/3/27
 */
@MyAnnotation(url = "/wuyuan1")
public class Wuyuan1Servlet implements Servlet {
    
    @Override
    public void init() {
        System.out.println("wuyuan1Servlet 的 init()方法执行了...");
    }
    @Override
    public void doGet(Request request, Response response) {
        // response.write("wuyuan1");
        // response.write("<h1>我的第一个网页</h1><p>这里标记第一个段落</p>");
        response.write("<p style=\"color: red; margin-left: 200px;font-size: 50pt;\">\n" +
                "This is a paragraph\n" +
                "</p>");
    }

    @Override
    public void doPost(Request request, Response response) {
        this.doGet(request, response);
    }
}
