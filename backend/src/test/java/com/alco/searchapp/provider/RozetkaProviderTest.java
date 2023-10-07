package com.alco.searchapp.provider;

import com.alco.searchapp.configuration.HttpClientConfiguration;
import com.alco.searchapp.configuration.ObjectMapperConfiguration;
import com.alco.searchapp.constants.ProviderTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RozetkaProviderTest {
    @Test
    public void getPathLinks() {
        var productPageDto = getProvider().getProductPages().blockFirst();
        Assertions.assertNotNull(productPageDto);
        Assertions.assertNotNull(productPageDto.getCategory());
        Assertions.assertEquals(ProviderTypeEnum.ROZETKA, productPageDto.getProvider());
    }

    @Test
    public void parsePageInfo() {
        var link = "https://rozetka.com.ua/ua/glenmorangie_5010494560282/p5006350/";
        var product = getProvider().parsePageInfo(link).block();

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Віскі Glenmorangie Original 10 років витримки 0.7 л 40% в подарунковій упаковці (5010494560282)", product.getName());
        Assertions.assertEquals("5010494560282", product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertNotNull(product.getDescription());
        Assertions.assertEquals(700, product.getVolume());
        Assertions.assertEquals("Glenmorangie", product.getBrand());
        Assertions.assertEquals(40.0f, product.getAbv());
        Assertions.assertEquals("Великобританія", product.getCountry());
        Assertions.assertEquals(2, product.getImageUrls().size());
        Assertions.assertTrue(product.getRating() > 0);
        Assertions.assertTrue(product.getVotes() > 0);
    }

    private RozetkaProvider getProvider() {
        return new RozetkaProvider(new ObjectMapperConfiguration().objectMapper(), getLinkParser());
    }

    private HtmlLinkParser getLinkParser() {
        return new HtmlLinkParser(new HttpClientConfiguration().client());
    }
}
