import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import okhttp3.*;

public class MyApiIT {

    @Test
    public void testGetEndpoint() throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://localhost:8080/sampleJakartaEEApp/api/hello-world")
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals(200, response.code());
            String responseBody = response.body().string();
            assertEquals("Hello, World!", responseBody);
        }
    }
}
