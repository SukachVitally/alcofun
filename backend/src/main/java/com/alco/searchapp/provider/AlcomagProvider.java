package com.alco.searchapp.provider;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.provider.dto.LdJsonDto;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.provider.dto.alcomag.ProductData;
import com.alco.searchapp.utils.AlcoholByVolumeUtil;
import com.alco.searchapp.utils.VolumeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

// todo: refactor it
@Slf4j
@Component
@RequiredArgsConstructor
public class AlcomagProvider implements IProvider {
    private static final String URL = "https://alcomag.ua";

    private static final List<Pair<String, CategoryTypeEnum>> PATHS = List.of(
            Pair.of("https://alcomag.ua/ua/krepkie-napitki/rom/filter/in_stock-is-y/apply/", CategoryTypeEnum.RUM),
            Pair.of("https://alcomag.ua/ua/vino/filter/in_stock-is-y/apply/", CategoryTypeEnum.VINE),
            Pair.of("https://alcomag.ua/ua/vino/shampanskoe/filter/in_stock-is-y/apply/", CategoryTypeEnum.CHAMPAGNE),
            Pair.of("https://alcomag.ua/ua/krepkie-napitki/konyak/filter/in_stock-is-y/apply/", CategoryTypeEnum.COGNAC),
            Pair.of("https://alcomag.ua/ua/krepkie-napitki/viski/filter/in_stock-is-y/apply/", CategoryTypeEnum.WHISKEY),
            Pair.of("https://alcomag.ua/ua/krepkie-napitki/vodka/filter/in_stock-is-y/apply/", CategoryTypeEnum.VODKA),
            Pair.of("https://alcomag.ua/ua/likery-vermuty/liker/filter/in_stock-is-y/apply/", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://alcomag.ua/ua/krepkie-napitki/tekila/filter/in_stock-is-y/apply/", CategoryTypeEnum.TEQUILA),
            Pair.of("https://alcomag.ua/ua/krepkie-napitki/brendi/filter/in_stock-is-y/apply/", CategoryTypeEnum.BRANDY),
            Pair.of("https://alcomag.ua/ua/pivo/filter/in_stock-is-y/apply/", CategoryTypeEnum.BEER),
            Pair.of("https://alcomag.ua/ua/likery-vermuty/vermut/filter/in_stock-is-y/apply/", CategoryTypeEnum.VERMOUTH)
    );

    private static final String LIST_PAGE_GET_PARAMETER = "PAGEN_1";

    private static final String LIST_LINK_SELECTOR = ".inner_wrap a.thumb";

    private static final String PROPERTY_ABV_TITLE = "Міцність";

    private static final String PROPERTY_VOLUME_TITLE = "Об`єм";

    private static final String PROPERTY_COUNTRY_TITLE = "Країна";


    private final HtmlLinkParser linkParser;

    private final ObjectMapper mapper;

    @Override
    public ProviderTypeEnum getType() {
        return ProviderTypeEnum.ALCOMAG;
    }

    @Override
    public Flux<Product> getList() {
        return Flux.fromIterable(PATHS)
                .flatMap(pair -> Flux
                        .fromIterable(new ProductLinkCollector(pair.getFirst(), LIST_LINK_SELECTOR, LIST_PAGE_GET_PARAMETER, ""))
                        .flatMap(Flux::fromIterable)
                        .map(url -> URL + url)
                        .flatMap(url -> linkParser.parse(url)
                                .map(document -> this.parse(document, pair.getSecond(), url))
                        )
                );
    }

    public Product parse(Document document, CategoryTypeEnum category, String url) {
        var info = document.select("script[type=application/ld+json]").stream()
                .map(Element::data)
                .filter(content -> {
                    try {
                        var dto = mapper.readValue(content, LdJsonDto.class);
                        return dto.getContext().contains("schema.org") && dto.getType().equals("Product");
                    } catch (JsonProcessingException e) {
                        return false;
                    }
                })
                .findAny()
                .map(content -> {
                    try {
                        return mapper.readValue(content, ProductData.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .orElse(new ProductData());

        var properties = document.select(".properties .properties__item").stream().map(element -> {
                var key = Optional.ofNullable(element.selectFirst(".properties__title"))
                        .map(Element::html).orElse(null);
                var value = Optional.ofNullable(element.selectFirst(".properties__value a"))
                        .map(Element::html).orElse(null);
                if (key == null || value == null) {
                    return null;
                }
                return Pair.of(key, value);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        return Product.builder()
                .ean(info.getEan())
                .link(url)
                .name(info.getName())
                .price(info.getOffers().getPrice())
                .description(info.getDescription())
                .brand(info.getBrand().getName())
                .volume(properties.containsKey(PROPERTY_VOLUME_TITLE) ? VolumeUtils.parse(properties.get(PROPERTY_VOLUME_TITLE)) : null)
                .abv(properties.containsKey(PROPERTY_ABV_TITLE) ? AlcoholByVolumeUtil.parse(properties.get(PROPERTY_ABV_TITLE)) : null)
                .country(properties.getOrDefault(PROPERTY_COUNTRY_TITLE, null))
                .category(category)
                .imageUrls(getImages(document))
                .providerType(this.getType())
                .build();
    }

    private List<String> getImages(Element doc) {
        return doc.select("img.product-detail-gallery__picture").stream()
                .map(element -> element.attr("data-src"))
                .distinct()
                .map(path -> URL + path)
                .toList();
    }
}
