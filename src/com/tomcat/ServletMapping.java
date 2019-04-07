package com.tomcat;

/**
 * @author wuyuan
 * @version 1.0
 * @description 描述:存储要访问的servlet的信息的实体类
 * @date 2019/3/27 11:11
 */
public class ServletMapping {
    private String servletName;
    private String url;
    private String address;

    public ServletMapping(String servletName, String url, String address) {
        this.servletName = servletName;
        this.url = url;
        this.address = address;
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
