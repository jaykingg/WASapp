package config;

public class HostConfig {
    String http_root;
    String error403;
    String error404;
    String error500;

    HostConfig(String http_root, String error403, String error404, String error500) {
        this.http_root = http_root;
        this.error403 = error403;
        this.error404 = error404;
        this.error500 = error500;
    }

    public String getHttp_root() {
        return http_root;
    }
}
