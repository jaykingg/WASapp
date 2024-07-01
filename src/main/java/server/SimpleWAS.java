package server;

import config.HostConfig;
import handler.ClientHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SimpleWAS {
    private static final Logger logger = LoggerFactory.getLogger(SimpleWAS.class);
    public static Map<String, HostConfig> hostConfigs = new HashMap<>();
    private static int serverPort;

    public static void main(String[] args) {
        loadConfig();
        startServer();
    }

    private static void loadConfig() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/java/config/config.json")));
            JSONObject config = new JSONObject(content);

            serverPort = config.getInt("port");

            JSONObject hosts = config.getJSONObject("hosts");
            for (String host : hosts.keySet()) {
                JSONObject hostConfig = hosts.getJSONObject(host);
                HostConfig configObj = new HostConfig(
                        hostConfig.getString("http_root"),
                        hostConfig.getJSONObject("errors").toMap()
                );
                hostConfigs.put(host, configObj);
            }

        } catch (Exception e) {
            logger.error("Failed to load configuration", e);
            System.exit(1);
        }
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            logger.info("Server started on port {}", serverPort);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    logger.error("Error accepting client connection", e);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to start server", e);
        }
    }
}
