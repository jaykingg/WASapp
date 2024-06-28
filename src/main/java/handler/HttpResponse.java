package handler;

import java.io.PrintWriter;
import java.io.Writer;

public class HttpResponse {
    private PrintWriter out;

    public HttpResponse(PrintWriter out) {
        this.out = out;
    }

    public Writer getWriter() {
        return out;
    }

    public void sendError(int statusCode, String message) {
        out.println("HTTP/1.1 " + statusCode + " " + message);
        out.println("Content-Type: text/html; charset=UTF-8");
        out.println();
        out.println("<html><body><h1>" + message + "</h1></body></html>");
    }
}
