package servlet;

import handler.HttpRequest;
import handler.HttpResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentTimeServlet implements SimpleServlet {
    @Override
    public void service(HttpRequest req, HttpResponse res) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String message = "Current time: " + time;
        res.sendError(200, message);
    }
}
