package com.alco.searchapp.controller.api;

import com.alco.searchapp.BaseFunctionalTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest()
@AutoConfigureWebTestClient
public class CategoryControllerTest extends BaseFunctionalTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    void testGetAllCategoriesList() {
        addProducts();

        webClient.get()
                .uri("/api/category/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(4);
    }
}
