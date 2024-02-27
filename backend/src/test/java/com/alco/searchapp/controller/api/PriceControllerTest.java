package com.alco.searchapp.controller.api;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.repository.ProductRepository;
import com.alco.searchapp.repository.elastic.ProductSearchRepository;
import com.alco.searchapp.service.ElasticCollectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest()
@AutoConfigureWebTestClient
public class PriceControllerTest extends BaseFunctionalTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private ElasticCollectService elasticService;

    @Test
    void testGetAllCategoriesList() {
        cleanDatabase();
        productSearchRepository.deleteAll().block();
        addProducts();
        elasticService.refresh().block();

        var entity = productRepository.findAll().blockFirst();
        Assertions.assertNotNull(entity);

        webClient.get()
                .uri("/api/price/history/" + entity.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1);
    }
}
