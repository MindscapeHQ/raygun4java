import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import okhttp3.*;

public class MyApiIT {

    @Test
    public void testHandledException() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:8080/sampleJakartaEEApp/api/handled-exception")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals(200, response.code());
            String responseBody = response.body().string();
            assertEquals("Hello, World!", responseBody);
        }
    }

    @Test
    public void testUnhandledException() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:8080/sampleJakartaEEApp/api/unhandled-exception")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals(500, response.code());
        }
    }
}
