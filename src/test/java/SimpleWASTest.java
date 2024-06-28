import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import server.SimpleWAS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleWASTest {
    private static SimpleWAS server;

    @BeforeClass
    public static void setUp() throws Exception {
        server = new SimpleWAS();
        new Thread(() -> {
            try {
                server.main(new String[]{});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000); // 서버가 시작될 시간을 줍니다.
    }

    @AfterClass
    public static void tearDown() throws IOException {
        server.stop();
    }

    @Test
    public void testHelloServlet() throws IOException {
        Socket socket = new Socket("localhost", 8000);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println("GET /Hello?name=jayking HTTP/1.1");
        out.println("Host: localhost");
        out.println();
        out.flush();

        String responseLine;
        StringBuilder response = new StringBuilder();
        while ((responseLine = in.readLine()) != null) {
            response.append(responseLine).append("\n");
        }

        System.out.println(response);

        Assert.assertTrue(response.toString().contains("Hello, jayking"));

        in.close();
        out.close();
        socket.close();
    }

}
