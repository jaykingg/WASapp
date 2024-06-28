package servlet;

import handler.HttpRequest;
import handler.HttpResponse;

public interface SimpleServlet {
    void service(HttpRequest req, HttpResponse res);
}
