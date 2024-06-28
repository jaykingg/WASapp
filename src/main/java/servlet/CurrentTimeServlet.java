package servlet;

import handler.HttpRequest;
import handler.HttpResponse;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentTimeServlet implements SimpleServlet {
    @Override
    public void service(HttpRequest req, HttpResponse res) {
        try {
            Writer writer = res.getWriter();
            writer.write("Current Time: ");
            writer.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
