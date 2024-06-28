package handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String url;
    private String version;
    private Map<String, String> headers;
    private Map<String, String> parameters;

    public HttpRequest(BufferedReader in) throws IOException {
        headers = new HashMap<>();
        parameters = new HashMap<>();
        parseRequest(in);
    }

    private void parseRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return;
        }

        String[] requestParts = requestLine.split(" ");
        method = requestParts[0];
        url = requestParts[1];
        version = requestParts[2];

        String line;
        while (!(line = in.readLine()).isEmpty()) {
            int colonIndex = line.indexOf(": ");
            if (colonIndex != -1) {
                headers.put(line.substring(0, colonIndex), line.substring(colonIndex + 2));
            }
        }

        if (url.contains("?")) {
            String[] urlParts = url.split("\\?");
            url = urlParts[0];
            String[] paramPairs = urlParts[1].split("&");
            for (String pair : paramPairs) {
                String[] keyValue = pair.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }
}
