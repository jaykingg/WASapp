package servlet;

import http.HttpRequest;
import http.HttpResponse;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentTimeServlet implements SimpleServlet {
    @Override
    public void service(HttpRequest req, HttpResponse res) {
        PrintWriter writer = res.getWriter();
        writer.write("Current time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
