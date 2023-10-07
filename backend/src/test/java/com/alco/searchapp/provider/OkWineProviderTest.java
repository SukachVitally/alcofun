package com.alco.searchapp.provider;

import com.alco.searchapp.configuration.HttpClientConfiguration;
import com.alco.searchapp.configuration.ObjectMapperConfiguration;
import com.alco.searchapp.constants.ProviderTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

public class OkWineProviderTest {
    @Test
    public void getPathLinks() {
        var productPageDto = getProvider().getProductPages().blockFirst();
        Assertions.assertNotNull(productPageDto);
        Assertions.assertNotNull(productPageDto.getCategory());
        Assertions.assertEquals(ProviderTypeEnum.OKWINE, productPageDto.getProvider());
    }

    @Test
    public void parsePageInfo() {
        var link = "https://okwine.ua/ua/product/rom-dead-mans-fingers-dmf-black-375-07l?urlProduct=rom-dead-mans-fingers-dmf-black-375-07l";
        var product = getProvider().parsePageInfo(link).block();

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Ром Дед Менс Фінгерс (ДМФ), Блек / Dead Man’s Fingers (DMF), Black, 37.5%, 0.7л", product.getName());
        Assertions.assertEquals("5011166067054", product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertNotNull(product.getDescription());
        Assertions.assertEquals(700, product.getVolume());
        Assertions.assertEquals("Dead Man’s Fingers", product.getBrand());
        Assertions.assertEquals(37.5f, product.getAbv());
        Assertions.assertEquals("Кариби", product.getCountry());
        Assertions.assertEquals(2, product.getImageUrls().size());

    }

    private OkWineProvider getProvider() {
        return new OkWineProvider(new ObjectMapperConfiguration().objectMapper(), getClient(), new HtmlLinkParser(getClient()));
    }

    private WebClient getClient() {
        return new HttpClientConfiguration().client();
    }
}
