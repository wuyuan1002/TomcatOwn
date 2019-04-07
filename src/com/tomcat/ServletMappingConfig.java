package com.tomcat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyuan
 * @version 1.0
 * @description 描述:配置哪个url访问哪个servlet
 * @date 2019/3/27 11:13
 */
public class ServletMappingConfig {
    public static List<ServletMapping> servletMappingConfig = new ArrayList<>();

    static {
        servletMappingConfig.add(new ServletMapping("wuyuan1", "/wuyuan1", "com.client.wuyuan1Servlet"));
        servletMappingConfig.add(new ServletMapping("wuyuan2", "/wuyuan2", "com.client.wuyuan2Servlet"));
    }

}
