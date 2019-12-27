package com.tomcat.request;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 将数据返回给客户端
 *
 * @author wuyuan
 * @date 2019/3/27
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
