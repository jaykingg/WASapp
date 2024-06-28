package config;

public class HostConfig {
    String root;
    String error403;
    String error404;
    String error500;

    HostConfig(String root, String error403, String error404, String error500) {
        this.root = root;
        this.error403 = error403;
        this.error404 = error404;
        this.error500 = error500;
    }

    public String getRoot() {
        return root;
    }
}
