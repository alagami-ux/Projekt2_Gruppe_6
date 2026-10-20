package dk.gruppe6.oenskeskyen;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:h2:mem:startup",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
    })
class Projekt2Gruppe6ApplicationTests {
    @Value("${local.server.port}")
    private int port;

    @Test
    void homePageAndStylesheetAreServed() throws Exception {
        var client = HttpClient.newHttpClient();
        var home = client.send(HttpRequest.newBuilder(
            URI.create("http://localhost:" + port + "/")).GET().build(),
            HttpResponse.BodyHandlers.ofString());
        assertEquals(200, home.statusCode());
        assertTrue(home.body().contains("Ønskeskyen"));
        assertTrue(home.body().contains("/css/style.css"));
        var css = client.send(HttpRequest.newBuilder(
            URI.create("http://localhost:" + port + "/css/style.css")).GET().build(),
            HttpResponse.BodyHandlers.ofString());
        assertEquals(200, css.statusCode());
    }
}
