package handler;

import config.HostConfig;
import http.HttpRequest;
import http.HttpRequestImpl;
import http.HttpResponse;
import http.HttpResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.SimpleWAS;
import servlet.SimpleServlet;

import java.io.*;
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

            String requestLine = in.readLine();
            if (requestLine != null && requestLine.startsWith("GET")) {
                String[] tokens = requestLine.split(" ");
                String url = tokens[1];
                String host = getHost(in);

                HostConfig hostConfig = SimpleWAS.hostConfigs.get(host);
                if (hostConfig == null) {
                    sendError(out, 404, "Not Found", hostConfig);
                    return;
                }

                if (isAccessForbidden(url, hostConfig)) {
                    sendError(out, 403, "Forbidden", hostConfig);
                    return;
                }

                SimpleServlet servlet = ServletMapping.getServlet(url);
                if (servlet != null) {
                    HttpRequest req = new HttpRequestImpl(in, tokens);
                    HttpResponse res = new HttpResponseImpl(out);
                    servlet.service(req, res);
                } else {
                    File file = new File(hostConfig.getHttpRoot() + url);
                    if (!file.exists()) {
                        sendError(out, 404, "Not Found", hostConfig);
                        return;
                    }
                    sendFile(out, file);
                }
            } else {
                sendError(out, 400, "Bad Request", null);
            }

        } catch (IOException e) {
            logger.error("Error handling client request", e);
        }
    }

    private String getHost(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("Host:")) {
                return line.split(" ")[1].split(":")[0];
            }
            if (line.isEmpty()) {
                break;
            }
        }
        return null;
    }

    private void sendError(PrintWriter out, int code, String message, HostConfig hostConfig) {
        out.println("HTTP/1.1 " + code + " " + message);
        out.println("Content-Type: text/html");
        out.println();

        String errorPage = (hostConfig != null) ? hostConfig.getErrorPage(String.valueOf(code)) : null;
        if (errorPage != null) {
            File file = new File(hostConfig.getHttpRoot() + "/" + errorPage);
            if (file.exists()) {
                try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        out.println(line);
                    }
                } catch (IOException e) {
                    logger.error("Error reading error page file", e);
                }
                return;
            }
        }

        out.println("<html><body><h1>" + code + " " + message + "</h1></body></html>");
    }

    private void sendFile(PrintWriter out, File file) throws IOException {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println();

        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = fileReader.readLine()) != null) {
            out.println(line);
        }
        fileReader.close();
    }

    private boolean isAccessForbidden(String url, HostConfig hostConfig) {
        return url.contains("..") || url.endsWith(".exe");
    }
}
