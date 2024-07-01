package handler;

import config.ConfigManager;
import config.HostConfig;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String hostHeader = request.getHeader("Host").split(":")[0];
            if (hostHeader == null || !ConfigManager.getHostConfigs().containsKey(hostHeader)) {
                response.sendError(404, "Not Found");
                return;
            }

            HostConfig hostConfig = ConfigManager.getHostConfigs().get(hostHeader);
            RequestHandler.handleRequest(request, response, hostConfig);

        } catch (IOException e) {
            logger.error("Error handling client request", e);
        }
    }
}
