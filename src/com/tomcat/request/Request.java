package com.tomcat.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author wuyuan
 * @version 1.0
 * @description 描述:通过Tomcat传过来的客户端socket的输入流获取HTTP协议的请求数据
 * @date 2019/3/27 10:31
 */
public class Request {
    private String url;
    private String method;
    private String allHttpRequest;

    public Request(InputStream inputStream) {
        byte[] bytes = new byte[1024];
        int len;

        try {
            if ((len = inputStream.read(bytes)) != -1) {
                this.allHttpRequest = new String(bytes,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String httpHead = this.allHttpRequest.split("\n")[0];
        this.method = httpHead.split(" ")[0];
        this.url = httpHead.split(" ")[1];


        System.out.println("-----------------一次请求开始----------------------");
        System.out.println("----------------allHttpRequest--------------------");
        System.out.println(allHttpRequest);
        System.out.println("-------------------httpHead-----------------------");
        System.out.println(httpHead);
        System.out.println("--------------------method------------------------");
        System.out.println(method);
        System.out.println("---------------------url--------------------------");
        System.out.println(url);
        System.out.println("------------------一次请求结束---------------------\r\n\r\n");

    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getAllHttpRequest() {
        return allHttpRequest;
    }
}
