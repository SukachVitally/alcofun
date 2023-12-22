package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.dto.ProductProviderDto;
import com.alco.searchapp.product.ProductRefresher;
import com.alco.searchapp.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
public class CategoryServiceTest extends BaseFunctionalTest {
    @Autowired
    private CategoryService service;

    @Autowired
    private ElasticCollectService elasticService;

    @BeforeEach
    public void setup() {
        cleanDatabase();
        addProducts(); //todo: Just populate elasticsearch without database here.
        elasticService.refresh().block();
    }

    @Test
    public void getAvailableCategories() {
        addProducts();
        elasticService.refresh().block();

        var availableCategoryCount = service.getAvailableCategories().count().block();

        Assertions.assertEquals(4, availableCategoryCount);
    }
}
