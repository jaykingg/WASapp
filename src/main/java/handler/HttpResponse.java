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

    public void setStatus(int statusCode) {
        out.println("HTTP/1.1 " + statusCode + " " + getReasonPhrase(statusCode));
    }

    public void setContentType(String contentType) {
        out.println("Content-Type: " + contentType);
    }

    public void sendError(int statusCode, String message) {
        setStatus(statusCode);
        setContentType("text/html");
        out.println();
        out.println("<html><body><h1>" + message + "</h1></body></html>");
    }

    public void flush() {  // 수정: 응답을 플러시하는 메서드 추가
        out.flush();
    }

    public void close() {  // 수정: 출력 스트림을 클로즈하는 메서드 추가
        out.close();
    }

    private String getReasonPhrase(int statusCode) {
        switch (statusCode) {
            case 200:
                return "OK";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            default:
                return "";
        }
    }
}
