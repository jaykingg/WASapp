import handler.ServletMapping;
import http.HttpRequest;
import http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.SimpleWAS;
import servlet.SimpleServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SimpleWASTest {

    private Thread serverThread;

    @Before
    public void setUp() throws Exception {
        serverThread = new Thread(() -> SimpleWAS.main(null));
        serverThread.start();
        Thread.sleep(2000);
    }

    @After
    public void tearDown() {
        serverThread.interrupt();
    }

    @Test
    public void testCurrentTimeServlet() throws Exception {
        URL url = new URL("http://localhost:8080/CurrentTime");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_OK, responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        assertEquals(true, content.toString().contains("Current time"));
    }

    @Test
    public void testNotFoundError() throws Exception {
        URL url = new URL("http://localhost:8080/nonexistent");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        assertTrue(content.toString().contains("404 Not Found"));
    }

    @Test
    public void testForbiddenError() throws Exception {
        URL url = new URL("http://localhost:8080/../forbidden");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_FORBIDDEN, responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        assertTrue(content.toString().contains("403 Forbidden"));
    }

    @Test
    public void testServletMapping() throws Exception {
        SimpleServlet mockServlet = mock(SimpleServlet.class);
        ServletMapping.addServlet("/test", mockServlet);

        URL url = new URL("http://localhost:8080/test");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, responseCode);

        verify(mockServlet, times(1)).service(any(HttpRequest.class), any(HttpResponse.class));
    }
}
