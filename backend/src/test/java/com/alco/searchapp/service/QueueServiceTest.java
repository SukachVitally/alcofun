package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.provider.dto.ProductPageDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class QueueServiceTest extends BaseFunctionalTest {
    @Autowired
    private QueueService service;

    @Test
    public void sendProductPage() {
        var res = service.sendProductPage(ProductPageDto.builder()
                        .category(CategoryTypeEnum.RUM)
                        .url("some url")
                        .provider(ProviderTypeEnum.ALCOMAG)
                        .build())
                .block();
        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.hasOffset());
    }
}
