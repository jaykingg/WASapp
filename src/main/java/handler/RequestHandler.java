package handler;

import config.HostConfig;
import servlet.CurrentTimeServlet;
import servlet.HelloServlet;
import servlet.SimpleServlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private static final Map<String, SimpleServlet> servlets = new HashMap<>();

    static {
        servlets.put("/Hello", new HelloServlet());
        servlets.put("/CurrentTime", new CurrentTimeServlet());
    }

    public static void handleRequest(HttpRequest req, HttpResponse res, HostConfig hostConfig) {
        if (servlets.containsKey(req.getUrl())) {
            SimpleServlet servlet = servlets.get(req.getUrl());
            servlet.service(req, res);
            return;
        }

        File file = new File(hostConfig.getRoot() + req.getUrl());
        if (!file.exists()) {
            res.sendError(404, "Not Found");
            return;
        }

        // 보안 규칙 처리
        if (req.getUrl().contains("../") || req.getUrl().endsWith(".exe")) {
            res.sendError(403, "Forbidden");
            return;
        }

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String responseLine;
            PrintWriter writer = (PrintWriter) res.getWriter();
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html; charset=UTF-8");
            writer.println();

            while ((responseLine = fileReader.readLine()) != null) {
                writer.println(responseLine);
            }
        } catch (IOException e) {
            res.sendError(500, "Internal Server Error");
        }
    }
}
