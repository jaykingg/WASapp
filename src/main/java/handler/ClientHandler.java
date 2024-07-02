package handler;

import config.HostConfig;
import http.HttpRequest;
import http.HttpRequestImpl;
import http.HttpResponse;
import http.HttpResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servlet.SimpleServlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
             OutputStream outStream = clientSocket.getOutputStream()) {

            String requestLine = in.readLine();
            if (requestLine != null && requestLine.startsWith("GET")) {
                String[] tokens = requestLine.split(" ");
                String url = tokens[1];
                String host = getHost(in);

                HostConfig hostConfig = server.SimpleWAS.hostConfigs.get(host);
                if (hostConfig == null) {
                    sendError(outStream, 404, "Not Found", null);
                    return;
                }

                if (isAccessForbidden(url, hostConfig)) {
                    sendError(outStream, 403, "Forbidden", hostConfig);
                    return;
                }

                SimpleServlet servlet = ServletMapping.getServlet(url);
                if (servlet != null) {
                    HttpRequest req = new HttpRequestImpl();
                    HttpResponse res = new HttpResponseImpl(outStream);
                    servlet.service(req, res);
                } else {
                    File file = new File(hostConfig.getHttpRoot() + url);
                    if (!file.exists()) {
                        sendError(outStream, 404, "Not Found", hostConfig);
                        return;
                    }

                    sendFile(outStream, file);
                }
            } else {
                sendError(outStream, 400, "Bad Request", null);
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

    private void sendError(OutputStream out, int code, String message, HostConfig hostConfig) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        writer.println("HTTP/1.1 " + code + " " + message);
        writer.println("Content-Type: text/html");
        writer.println();

        String errorPage = (hostConfig != null) ? hostConfig.getErrorPage(String.valueOf(code)) : null;
        if (errorPage != null) {
            File file = new File("resources/" + errorPage);
            if (file.exists()) {
                sendFile(out, file);
                return;
            }
        }

        writer.println("<html><body><h1>" + code + " " + message + "</h1></body></html>");
        writer.flush();
    }

    private void sendFile(OutputStream out, File file) throws IOException {
        String contentType = getContentType(file);
        PrintWriter writer = new PrintWriter(out);
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: " + contentType);
        writer.println();

        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = fileReader.readLine()) != null) {
            writer.println(line);
        }
        fileReader.close();
        writer.flush();
    }

    private String getContentType(File file) {
        String fileName = file.getName();
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".json")) {
            return "application/json";
        } else if (fileName.endsWith(".png")) {
            return "image/png";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "text/plain";
        }
    }

    private boolean isAccessForbidden(String url, HostConfig hostConfig) {
        if (url.contains("..") || url.endsWith(".exe")) {
            return true;
        }
        return false;
    }
}
