package com.alco.searchapp.controller;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ListResponseDto;
import com.alco.searchapp.repository.ProductProviderRepository;
import com.alco.searchapp.repository.ProductRepository;
import com.alco.searchapp.repository.elastic.ProductSearchRepository;
import com.alco.searchapp.service.ElasticCollectService;
import com.alco.searchapp.service.ProductSearchService;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest()
@AutoConfigureWebTestClient
public class SeoPageControllerTest extends BaseFunctionalTest {
    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ElasticCollectService elasticCollectService;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private ProductProviderRepository productProviderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void getMainPage() {
        var body = webClient.get()
                .uri("/seo/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody().returnResult()
                .getResponseBody();
        Assertions.assertNotNull(body);
        var content = new String(body, StandardCharsets.UTF_8);
        assertContains(content, "<title>Alcoprice UA</title>");
        assertContains(content,
                "<h1>AlcoPrice UA: ваш найкращий агрегатор алкогольних напоїв в Україні!</h1>");
        assertNotContains(content, BaseFunctionalTest.EAN1);
    }

    @Test
    void getProductListPage() {
        cleanDatabase();
        productSearchRepository.deleteAll().block();
        addProducts();
        elasticCollectService.refresh().block();

        var body = webClient.get()
                .uri("/seo/products/")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody().returnResult()
                .getResponseBody();
        Assertions.assertNotNull(body);
        var content = new String(body, StandardCharsets.UTF_8);
        assertContains(content, "<title>Alcoprice UA список продуктів</title>");
        assertContains(content, "<h1>Alcoprice UA агрегатор алкогольних продуктів в Україні</h1>");
        assertContains(content, BaseFunctionalTest.EAN1);
    }

    @Test
    void getProductPage() {
        cleanDatabase();
        productSearchRepository.deleteAll().block();
        addProducts();
        elasticCollectService.refresh().block();

        var products = productSearchService.getProducts(Filterable.builder().build(),
                        PageRequest.of(1, 1))
                .map(ListResponseDto::getResults)
                .block();

        Assertions.assertNotNull(products);
        Assertions.assertFalse(products.isEmpty());
        var product = products.get(0);

        var body = webClient.get()
                .uri("/seo/products/" + product.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody().returnResult()
                .getResponseBody();
        Assertions.assertNotNull(body);
        var content = new String(body, StandardCharsets.UTF_8);
        assertContains(content, product.getName());
        assertContains(content, product.getEan());
        assertContains(content, product.getCategoryName());
    }

    @Test
    void getProductPageWithoutPrice() {
        cleanDatabase();
        productSearchRepository.deleteAll().block();
        addProducts();
        productProviderRepository.deleteAll().block();
        elasticCollectService.refresh().block();



        var product = productRepository.findOneByEan(EAN1).block();
        Assertions.assertNotNull(product);

        var body = webClient.get()
                .uri("/seo/products/" + product.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody().returnResult()
                .getResponseBody();
        Assertions.assertNotNull(body);
        var content = new String(body, StandardCharsets.UTF_8);
        assertContains(content, product.getName());
        assertContains(content, product.getEan());
        assertNotContains(content, "Ціна");
    }

    @Test
    void getProductPageNotFound() {
        var body = webClient.get()
                .uri("/seo/products/123456")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody().returnResult()
                .getResponseBody();
        Assertions.assertNotNull(body);
        var content = new String(body, StandardCharsets.UTF_8);
        assertContains(content, "Вибачте, але сторінку, яку ви шукаєте, не знайдено...");
    }
}
