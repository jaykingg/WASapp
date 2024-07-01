package http;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestImpl implements HttpRequest {
    private final Map<String, String> parameters;

    public HttpRequestImpl(BufferedReader in, String[] tokens) {
        parameters = parseParameters(tokens[1]);
    }

    private Map<String, String> parseParameters(String url) {
        Map<String, String> params = new HashMap<>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String[] paramPairs = urlParts[1].split("&");
            for (String pair : paramPairs) {
                String[] keyValue = pair.split("=");
                params.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
            }
        }
        return params;
    }

    @Override
    public String getParameter(String name) {
        return parameters.get(name);
    }
}