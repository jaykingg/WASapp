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
            throw new IOException("Invalid request line: " + requestLine);  // 수정: 예외 던지기
        }

        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 3) {  // 수정: 요청 라인의 유효성 검사 추가
            throw new IOException("Invalid request line: " + requestLine);
        }

        method = requestParts[0];
        url = requestParts[1];
        version = requestParts[2];

        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {  // 수정: 헤더 라인의 유효성 검사 추가
            int colonIndex = line.indexOf(": ");
            if (colonIndex != -1) {
                String headerName = line.substring(0, colonIndex).trim();
                String headerValue = line.substring(colonIndex + 2).trim();
                headers.put(headerName, headerValue);
            }
        }

        if (url.contains("?")) {
            String[] urlParts = url.split("\\?");
            url = urlParts[0];
            String[] paramPairs = urlParts[1].split("&");
            for (String pair : paramPairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {  // 수정: 쿼리 파라미터의 유효성 검사 추가
                    parameters.put(keyValue[0], keyValue[1]);
                }
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
