package com.tomcat.request;

/**
 * @author wuyuan
 * @date 2019/3/27
 */
public interface Servlet {
    
    void init();
    
    void doGet(Request request, Response response);
    
    void doPost(Request request, Response response);
    
    default void service(Request request, Response response) {
        String GET = "GET";
        String POST = "POST";
        if (GET.equalsIgnoreCase(request.getMethod())) {
            this.doGet(request, response);
        } else if (POST.equalsIgnoreCase(request.getMethod())) {
            this.doPost(request, response);
        }
    }
    
}
