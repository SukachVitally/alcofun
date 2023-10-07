package com.alco.searchapp.provider;

import com.alco.searchapp.configuration.HttpClientConfiguration;
import com.alco.searchapp.configuration.ObjectMapperConfiguration;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

public class OkWineProviderTest {
    @Test
    public void getPathLinks() {
//        var product = getProvider().getProductPages().blockFirst();
//        Assertions.assertNotNull(product);
//        Assertions.assertNotNull(product.getEan());
//        Assertions.assertNotNull(product.getPrice());
//        Assertions.assertFalse(product.getImageUrls().isEmpty());
    }

    @Test
    public void parseLink() {
//        var url = "https://okwine.ua/ua/product/rom-dead-mans-fingers-dmf-black-375-07l?urlProduct=rom-dead-mans-fingers-dmf-black-375-07l";
//        var document = new HtmlLinkParser(getClient()).parse(url).blockOptional().orElseThrow();
//
//        var product = getProvider().parse(document, CategoryTypeEnum.RUM, url);
//        Assertions.assertNotNull(product);
//        Assertions.assertEquals("Ром Дед Менс Фінгерс (ДМФ), Блек / Dead Man’s Fingers (DMF), Black, 37.5%, 0.7л", product.getName());
//        Assertions.assertEquals("5011166067054", product.getEan());
//        Assertions.assertNotNull(product.getPrice());
//        Assertions.assertNotNull(product.getDescription());
//        Assertions.assertEquals(700, product.getVolume());
//        Assertions.assertEquals("Dead Man’s Fingers", product.getBrand());
//        Assertions.assertEquals(37.5f, product.getAbv());
//        Assertions.assertEquals("Кариби", product.getCountry());
//        Assertions.assertEquals(url, product.getLink());
//        Assertions.assertEquals(2, product.getImageUrls().size());
//        Assertions.assertEquals(ProviderTypeEnum.OKWINE, product.getProviderType());

    }

    private OkWineProvider getProvider() {
        return new OkWineProvider(new ObjectMapperConfiguration().objectMapper(), getClient(), new HtmlLinkParser(getClient()));
    }

    private WebClient getClient() {
        return new HttpClientConfiguration().client();
    }
}
