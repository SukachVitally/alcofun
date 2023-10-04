package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
public class BestPriceServiceTest extends BaseFunctionalTest {
    @Autowired
    private BestPriceService service;

    @Test
    public void getBestPriceNotHot() {
        addProducts();



    }
}
