package com.client;

import com.tomcat.Request;
import com.tomcat.Response;
import com.tomcat.Servlet;

/**
 * @author wuyuan
 * @version 1.0
 * @description 描述:
 * @date 2019/3/27 12:07
 */
public class wuyuan2Servlet implements Servlet {
    @Override
    public void doGet(Request request, Response response) {
        response.write("wuyuan2");
    }

    @Override
    public void doPost(Request request, Response response) {

    }
}
