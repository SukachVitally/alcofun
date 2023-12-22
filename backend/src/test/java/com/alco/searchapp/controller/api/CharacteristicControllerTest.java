package com.alco.searchapp.controller.api;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.service.ElasticCollectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest()
@AutoConfigureWebTestClient
public class CharacteristicControllerTest extends BaseFunctionalTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ElasticCollectService elasticService;

    @BeforeEach
    public void setup() {
        cleanDatabase();
        addProducts();
        elasticService.refresh().block();
    }

    @Test
    void testGetAllCharacteristicsForTypeList() {
        webClient.get()
                .uri("/api/characteristic/" + CharacteristicTypeEnum.ALCOHOL_BY_VOLUME.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3);
    }

    @Test
    void testGetAllCharacteristicsTypes() {
        webClient.get()
                .uri("/api/characteristic/types")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(4);
    }
}
