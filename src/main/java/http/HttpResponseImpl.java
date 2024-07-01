package http;

import java.io.PrintWriter;

public class HttpResponseImpl implements HttpResponse {
    private final PrintWriter writer;

    public HttpResponseImpl(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }
}
