package handler;

import config.ConfigManager;
import config.HostConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
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

            String hostHeader = request.getHeader("Host");
            if (hostHeader == null || !ConfigManager.getHostConfigs().containsKey(hostHeader)) {
                response.sendError(404, "Not Found");
                return;
            }

            HostConfig hostConfig = ConfigManager.getHostConfigs().get(hostHeader);
            RequestHandler.handleRequest(request, response, hostConfig);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
