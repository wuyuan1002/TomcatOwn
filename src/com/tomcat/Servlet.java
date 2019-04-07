package com.tomcat;

/**
 * @author wuyuan
 * @version 1.0
 * @description 描述:
 * @date 2019/3/27 10:58
 */
public interface Servlet {

    void doGet(Request request,Response response);
    void doPost(Request request,Response response);

    default void service(Request request,Response response){
        String GET = "GET";
        String POST = "POST";
        if (GET.equalsIgnoreCase(request.getMethod())) {
            this.doGet(request, response);
        } else if (POST.equalsIgnoreCase(request.getMethod())) {
            this.doPost(request, response);
        }
    }

}
