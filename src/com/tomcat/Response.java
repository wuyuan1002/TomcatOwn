package com.tomcat;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author wuyuan
 * @version 1.0
 * @description 描述:将数据返回给客户端
 * @date 2019/3/27 10:51
 */
public class Response {
    private OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void write(String data) {
        StringBuilder httpResponse = new StringBuilder();
        httpResponse.append("HTTP 200 OK\n")
                .append("Content-Type: text/html\n")
                .append("\r\n")
                .append("<html><body>")
                .append(data)
                .append("</body></html>");
        try {
            this.outputStream.write(httpResponse.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
