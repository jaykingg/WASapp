package server;

import config.ConfigManager;
import handler.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleWAS {
    private ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        SimpleWAS server = new SimpleWAS();
        server.start();
    }

    public void start() throws IOException {
        ConfigManager.loadConfig("src/main/java/config/config.json");
        serverSocket = new ServerSocket(ConfigManager.getPort());

        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    public void stop() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
}
