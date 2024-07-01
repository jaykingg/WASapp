package config;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static Map<String, HostConfig> hostConfigs;
    private static int port;

    public static void loadConfig(String configFilePath) throws IOException {
        String configContent = new String(Files.readAllBytes(Paths.get(configFilePath)));
        JSONObject config = new JSONObject(configContent);
        port = config.getInt("port");

        hostConfigs = new HashMap<>();
        JSONObject hosts = config.getJSONObject("hosts");
        for (String hostName : hosts.keySet()) {
            JSONObject hostConfig = hosts.getJSONObject(hostName);
            String http_root = hostConfig.getString("http_root");
            JSONObject errors = hostConfig.getJSONObject("errors");
            String error403 = errors.getString("403");
            String error404 = errors.getString("404");
            String error500 = errors.getString("500");
            hostConfigs.put(hostName, new HostConfig(http_root, error403, error404, error500));
        }
    }

    public static int getPort() {
        return port;
    }

    public static Map<String, HostConfig> getHostConfigs() {
        return hostConfigs;
    }
}
