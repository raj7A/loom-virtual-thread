package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
public class ControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGetHello() {
        webTestClient.get().uri("/process")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Slept for 100 ms");
    }

    @Test
    public void testGetHello_ResponseTime() {
        long startTime = System.currentTimeMillis();
        webTestClient.get().uri("/sleep/100")
                .accept(MediaType.TEXT_PLAIN)
                .exchange()
                .expectStatus().isOk();
        long endTime = System.currentTimeMillis();
        assertEquals(true, endTime - startTime >= 100);
    }
}