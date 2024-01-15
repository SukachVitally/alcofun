package com.alco.searchapp.provider;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class LinkParserTest extends AbstractProviderTest {
    @Test
    public void parseFozzyPage() {
        var url = "https://fozzyshop.ua/rom/31578-rom-cayo-grande-anekho-8414771850078.html";
        var html = linkParser().get(url).block();
        Assertions.assertNotEquals("", html);
    }

    @Disabled
    @Test
    public void parseRozetkaPage() {
        var url = "https://rozetka.com.ua/ua/glenmorangie_5010494560282/p5006350/";
        var html = linkParser().get(url).block();
        Assertions.assertNotEquals("", html);
    }

    @Test
    public void parseAuchanPage() {
        var url = "https://auchan.zakaz.ua/en/products/liker-egermaster-700ml-nimechchina--00250009134217/";
        var html = linkParser().get(url).block();
        Assertions.assertNotEquals("", html);
    }

    @Test
    public void parseSilpoPage() {
        var url = "https://shop.silpo.ua/product/viski-jameson-58113";
        var html = linkParser().get(url).block();
        Assertions.assertNotEquals("", html);
    }
}
