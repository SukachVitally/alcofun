package com.alco.searchapp.provider;

import com.alco.searchapp.configuration.HttpClientConfiguration;
import com.alco.searchapp.configuration.ObjectMapperConfiguration;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.ProviderBlock;
import com.alco.searchapp.properites.RozetkaProperties;
import java.util.List;
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
        Assertions.assertEquals("Віскі Glenmorangie Original 10 років витримки 0.7 л 40% в подарунковій упаковці", product.getName());
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

    @Test
    public void parsePageInfoWith1LVolume() {
        var link = "https://rozetka.com.ua/ua/jim_beam_5010196092142/p10484842/";
        var product = getProvider().parsePageInfo(link).block();

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Віскі Jim Beam White 4 роки витримки 1 л 40%", product.getName());
        Assertions.assertEquals("5010196092142", product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertNotNull(product.getDescription());
        Assertions.assertEquals(1000, product.getVolume());
        Assertions.assertEquals("Jim Beam", product.getBrand());
        Assertions.assertEquals(40.0f, product.getAbv());
        Assertions.assertEquals("США", product.getCountry());
        Assertions.assertTrue(product.getImageUrls().size() > 0);
        Assertions.assertTrue(product.getRating() > 0);
        Assertions.assertTrue(product.getVotes() > 0);
    }

    private RozetkaProperties getProperties() {
        var block = new ProviderBlock();
        block.setType(CategoryTypeEnum.WHISKEY);
        block.setUrl("https://rozetka.com.ua/ua/viski/c4649130/");
        var properties = new RozetkaProperties();
        properties.setBlocks(List.of(block));
        return properties;
    }

    private RozetkaProvider getProvider() {
        return new RozetkaProvider(new ObjectMapperConfiguration().objectMapper(), getLinkParser(), getProperties());
    }

    private HtmlLinkParser getLinkParser() {
        return new HtmlLinkParser(new HttpClientConfiguration().client());
    }
}
