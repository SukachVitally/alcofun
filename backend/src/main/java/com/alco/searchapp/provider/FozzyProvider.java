package com.alco.searchapp.provider;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.provider.FozzyProperties;
import com.alco.searchapp.provider.dto.ProductInfo;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.alco.searchapp.utils.AlcoholByVolumeUtil;
import com.alco.searchapp.utils.VolumeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jsoup.nodes.Element;

@Slf4j
@Component
@RequiredArgsConstructor
public class FozzyProvider implements IProvider {
    private final LinkParser linkParser;

    private final FozzyProperties properties;

    @Override
    public ProviderTypeEnum getType() {
        return ProviderTypeEnum.FOZZY;
    }

    public Function<Integer, String> createLinkFunction(String url) {
        return (Integer page) -> String.format("%s?page=%s", url, page);
    }

    public List<String> parseProductList(Document document) {
        return document.select(".js-product-miniature-wrapper:not(.product_grey) .thumbnail-container > a").stream()
                .map(element -> element.attr("href"))
                .toList();
    }

    @Override
    public Flux<ProductPageDto> getProductPages() {
        return Flux.fromIterable(properties.getBlocks())
                .flatMap(block -> Flux
                        .fromIterable(() -> new CategoryProductListParser(createLinkFunction(block.getUrl()), this::parseProductList))
                        .flatMap(Flux::fromIterable)
                        .map(url -> ProductPageDto.builder()
                                .url(url)
                                .provider(getType())
                                .category(block.getType())
                                .build()));
    }

    @Override
    public Mono<ProductInfo> parsePageInfo(String url) {
        return linkParser.parseHtml(url).map(this::parseDocument);
    }

    private ProductInfo parseDocument(Document document) {
        var characteristics = parseCharacteristics(document);
        return ProductInfo.builder()
                .ean(parseEan(document))
                .name(parseName(document))
                .price(parsePrice(document))
                .brand(characteristics.getOrDefault("Бренд", null))
                .country(characteristics.getOrDefault("Країна", null))
                .abv(Optional.ofNullable(characteristics.getOrDefault("Міцність алкоголю, %", null))
                        .map(AlcoholByVolumeUtil::parse)
                        .orElse(null))
                .volume(Optional.ofNullable(characteristics.getOrDefault("Об`єм, л", null))
                        .map(VolumeUtils::parse)
                        .orElse(null))
                .imageUrls(parseImages(document))
                .build();
    }

    private String parseEan(Document document) {
        return Optional.ofNullable(document.selectFirst(".global-features .data-sheet dd.value"))
                .map(Element::text)
                .map(str -> str.replace("Штрихкод:", "").trim())
                .orElse(null);
    }

    private String parseName(Document document) {
        return Optional.ofNullable(document.selectFirst("h1.page-title>span"))
                .map(Element::text)
                .orElse(null);
    }

    private Double parsePrice(Document document) {
        return Optional.ofNullable(document.selectFirst("meta[property=product:price:amount]"))
                .map(element -> element.attr("content"))
                .map(Double::valueOf)
                .orElse(null);
    }

    private List<String> parseImages(Document document) {
        return document.select("meta[property=og:image]").stream()
                .map(element -> element.attr("content"))
                .toList();
    }

    private Map<String, String> parseCharacteristics(Document document) {
        Elements names = document.select(".product-features dt.name");
        Elements values = document.select(".product-features dd.value");

        return IntStream.range(0, names.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> names.get(i).text(),
                        i -> values.get(i).text()));
    }
}