package com.alco.searchapp.controller.api;

import com.alco.searchapp.BaseFunctionalTest;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest()
@AutoConfigureWebTestClient
public class SeoControllerTest extends BaseFunctionalTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    void getSitemap() {
        addProducts();

        var body = webClient.get()
                .uri("/api/seo/sitemap.xml")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_XML)
                .expectBody().returnResult()
                .getResponseBody();
        Assertions.assertNotNull(body);
        var content = new String(body, StandardCharsets.UTF_8);
        assertContains(content, "https://alcoprice.com.ua/products/");
    }
}
