package http;

import java.io.OutputStream;
import java.io.PrintWriter;

public class HttpResponseImpl implements HttpResponse {
    private final PrintWriter writer;

    public HttpResponseImpl(OutputStream outputStream) {
        this.writer = new PrintWriter(outputStream, true);
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }
}
