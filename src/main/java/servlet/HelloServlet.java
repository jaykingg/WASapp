package servlet;

import handler.HttpRequest;
import handler.HttpResponse;

import java.io.IOException;
import java.io.Writer;

public class HelloServlet implements SimpleServlet {
    @Override
    public void service(HttpRequest req, HttpResponse res) {
        try {
            Writer writer = res.getWriter();
            writer.write("Hello, ");
            writer.write(req.getParameter("name"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
