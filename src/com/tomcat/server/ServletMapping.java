package com.tomcat.server;

/**
 * 存储要访问的servlet的信息的实体类
 *
 * @author wuyuan
 * @version 1.0
 * @date 2019/3/27 11:11
 */
class ServletMapping {
    private String url;
    private Class<?> clazz;
    
    ServletMapping(String url, Class<?> clazz) {
        this.url = url;
        this.clazz = clazz;
    }
    
    String getUrl() {
        return url;
    }
    
    Class<?> getClazz() {
        return clazz;
    }
    
}
