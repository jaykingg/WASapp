package handler;

import servlet.CurrentTimeServlet;
import servlet.SimpleServlet;

import java.util.HashMap;
import java.util.Map;

public class ServletMapping {
    private static final Map<String, SimpleServlet> servlets = new HashMap<>();

    static {
        servlets.put("/CurrentTime", new CurrentTimeServlet());
    }

    public static SimpleServlet getServlet(String url) {
        return servlets.get(url);
    }
}
