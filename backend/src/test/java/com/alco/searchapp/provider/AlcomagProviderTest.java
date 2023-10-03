package com.alco.searchapp.provider;

import com.alco.searchapp.configuration.HttpClientConfiguration;
import com.alco.searchapp.configuration.ObjectMapperConfiguration;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AlcomagProviderTest {
    @Test
    public void getPathLinks() {
        var product = getProvider().getList().blockFirst();
        Assertions.assertNotNull(product);
        Assertions.assertNotNull(product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertFalse(product.getImageUrls().isEmpty());
    }

    @Test
    public void parseLink() {
        var link = "https://alcomag.ua/ua/rom-bacardi-oakheart-1-l/";
        var product = getHtmlLinkParser().parse(link)
                .map(document -> getProvider().parse(document, CategoryTypeEnum.RUM, link))
                .block();

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Ром Bacardi Oakheart 1 л", product.getName());
        Assertions.assertEquals("5010677740203", product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertNotNull(product.getDescription());
        Assertions.assertEquals(1000, product.getVolume());
        Assertions.assertEquals("Bacardi", product.getBrand());
        Assertions.assertEquals(35.0f, product.getAbv());
        Assertions.assertEquals("Великобританія", product.getCountry());
        Assertions.assertEquals(link, product.getLink());
        Assertions.assertEquals(2, product.getImageUrls().size());
        Assertions.assertEquals(ProviderTypeEnum.ALCOMAG, product.getProviderType());
        Assertions.assertTrue(product.getRating() > 0);
        Assertions.assertTrue(product.getVotes() > 0);
    }

    private AlcomagProvider getProvider() {
        return new AlcomagProvider(getHtmlLinkParser(), new ObjectMapperConfiguration().objectMapper());
    }

    private HtmlLinkParser getHtmlLinkParser() {
        return new HtmlLinkParser(new HttpClientConfiguration().client());
    }
}
