package handler;

import config.HostConfig;
import servlet.CurrentTimeServlet;
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
        servlets.put("/CurrentTime", new CurrentTimeServlet());
    }

    public static void handleRequest(HttpRequest req, HttpResponse res, HostConfig hostConfig) {
        String path = req.getUrl().split("\\?")[0]; // 쿼리 파라미터 제거

        if (servlets.containsKey(path)) {
            SimpleServlet servlet = servlets.get(path);
            servlet.service(req, res);
            return;
        }

        if (path.contains("../") || path.endsWith(".exe")) {
            res.sendError(403, "Forbidden");
            return;
        }

        File file = new File(hostConfig.getRoot() + path);
        if (!file.exists()) {
            res.sendError(404, "Not Found");
            return;
        }

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String responseLine;
            PrintWriter writer = (PrintWriter) res.getWriter();
            res.setStatus(200);
            res.setContentType("text/html");
            writer.println();

            while ((responseLine = fileReader.readLine()) != null) {
                writer.println(responseLine);
            }
        } catch (IOException e) {
            res.sendError(500, "Internal Server Error");
        }
    }
}
