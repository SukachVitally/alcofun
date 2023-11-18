package com.alco.searchapp.provider;

import com.alco.searchapp.configuration.HttpClientConfiguration;
import com.alco.searchapp.configuration.ObjectMapperConfiguration;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.AlcomagProperties;
import com.alco.searchapp.properites.ProviderBlock;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AlcomagProviderTest {
    @Test
    public void getPathLinks() {
        var productPageDto = getProvider().getProductPages().blockFirst();
        Assertions.assertNotNull(productPageDto);
        Assertions.assertEquals(ProviderTypeEnum.ALCOMAG, productPageDto.getProvider());
        Assertions.assertNotNull(productPageDto.getUrl());
        Assertions.assertNotNull(productPageDto.getCategory());
    }

    @Test
    public void parseLink() {
        var link = "https://alcomag.ua/ua/rom-bacardi-oakheart-1-l/";
        var product = getProvider().parsePageInfo(link).block();

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Ром Bacardi Oakheart 1 л", product.getName());
        Assertions.assertEquals("5010677740203", product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertNotNull(product.getDescription());
        Assertions.assertFalse(product.getDescription().contains("Alcomag"));
        Assertions.assertEquals(1000, product.getVolume());
        Assertions.assertEquals("Bacardi", product.getBrand());
        Assertions.assertEquals(35.0f, product.getAbv());
        Assertions.assertEquals("Великобританія", product.getCountry());
        Assertions.assertEquals(2, product.getImageUrls().size());
        Assertions.assertTrue(product.getRating() > 0);
        Assertions.assertTrue(product.getVotes() > 0);
    }

    @Test
    public void parseLinkWithCorrectDescription() {
        var link = "https://alcomag.ua/ua/pivo-praga-premium-pils-4-7-svetloe-zh-b-0-5-l/";
        var product = getProvider().parsePageInfo(link).block();

        Assertions.assertNotNull(product);
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertNotNull(product.getDescription());
        Assertions.assertFalse(product.getDescription().contains("Наші консультанти"));
        Assertions.assertFalse(product.getDescription().contains("Alcomag"));
    }

    private AlcomagProvider getProvider() {
        return new AlcomagProvider(getHtmlLinkParser(), new ObjectMapperConfiguration().objectMapper(), getProperties());
    }

    private AlcomagProperties getProperties() {
        var block = new ProviderBlock();
        block.setType(CategoryTypeEnum.RUM);
        block.setUrl("https://alcomag.ua/ua/krepkie-napitki/rom/filter/in_stock-is-y/apply/");
        var properties = new AlcomagProperties();
        properties.setBlocks(List.of(block));
        return properties;
    }

    private HtmlLinkParser getHtmlLinkParser() {
        return new HtmlLinkParser(new HttpClientConfiguration().client());
    }
}
