package config;

import java.util.Map;

public class HostConfig {
    private final String httpRoot;
    private final Map<String, Object> errorPages;

    public HostConfig(String httpRoot, Map<String, Object> errorPages) {
        this.httpRoot = httpRoot;
        this.errorPages = errorPages;
    }

    public String getHttpRoot() {
        return httpRoot;
    }

    public String getErrorPage(String statusCode) {
        return (String) errorPages.get(statusCode);
    }
}
