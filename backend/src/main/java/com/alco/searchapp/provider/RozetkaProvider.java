package com.alco.searchapp.provider;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.provider.dto.ProductInfo;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.alco.searchapp.provider.dto.rozetka.RozetkaData;
import com.alco.searchapp.utils.AlcoholByVolumeUtil;
import com.alco.searchapp.utils.VolumeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class RozetkaProvider implements IProvider  {
    private static final String LIST_LINK_SELECTOR = "a.goods-tile__picture";

    private static final String LIST_PAGE_GET_PARAMETER = "page";

    private static final String PAGE_DATA_SELECTOR = "script[data-seo=Product]";

    private static final String CHARACTERISTIC_VOLUME = "Об'єм";
    private static final String CHARACTERISTIC_ABV = "Міцність";
    private static final String CHARACTERISTIC_COUNTRY = "Країна";

    // todo: Move to config
    private static final List<Pair<String, CategoryTypeEnum>> PATHS = List.of(
            Pair.of("https://rozetka.com.ua/ua/viski/c4649130/", CategoryTypeEnum.WHISKEY),
            Pair.of("https://rozetka.com.ua/ua/tihoe-vino/c4649052/", CategoryTypeEnum.VINE),
            Pair.of("https://rozetka.com.ua/ua/shampanskoe-i-igristoe-vino/c4649058/", CategoryTypeEnum.CHAMPAGNE),
            Pair.of("https://rozetka.com.ua/ua/vermut/c4649064/", CategoryTypeEnum.VERMOUTH),
            Pair.of("https://rozetka.com.ua/ua/rom/c4649142/", CategoryTypeEnum.RUM),
            Pair.of("https://rozetka.com.ua/ua/liquor-vermouth-syrup/c4625409/", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://rozetka.com.ua/ua/vodka/c4649154/", CategoryTypeEnum.VODKA),
            Pair.of("https://rozetka.com.ua/ua/dgin/c4649166/", CategoryTypeEnum.GIN),
            Pair.of("https://rozetka.com.ua/ua/tekila-i-meskal/c4649148/", CategoryTypeEnum.TEQUILA),
            Pair.of("https://rozetka.com.ua/ua/konyak-i-brendi/c4649136/", CategoryTypeEnum.COGNAC),
            Pair.of("https://rozetka.com.ua/ua/absent/c4649172/", CategoryTypeEnum.ABSENT),
            Pair.of("https://rozetka.com.ua/ua/sake/c4674322/", CategoryTypeEnum.SAKE),
            Pair.of("https://rozetka.com.ua/ua/chacha/c4649160/", CategoryTypeEnum.CHACHA),
            Pair.of("https://rozetka.com.ua/ua/grappa-i-kalvados/c4649178/", CategoryTypeEnum.GRAPPA)
            );

    private final ObjectMapper mapper;

    private final HtmlLinkParser linkParser;

    @Override
    public Flux<ProductPageDto> getProductPages() {
        return Flux.fromIterable(PATHS)
                .flatMap(pair -> Flux
                        .fromIterable(new ProductLinkCollector(pair.getFirst(), LIST_LINK_SELECTOR, LIST_PAGE_GET_PARAMETER, ""))
                        .flatMap(Flux::fromIterable)
                        .map(url -> ProductPageDto.builder()
                                .url(url)
                                .provider(getType())
                                .category(pair.getSecond())
                                .build()));
    }

    @Override
    public Mono<ProductInfo> parsePageInfo(String url) {
        return linkParser.parse(url).map(this::parseDocument);
    }

    @Override
    public ProviderTypeEnum getType() {
        return ProviderTypeEnum.ROZETKA;
    }

    public ProductInfo parseDocument(Document document) {
        return document.select(PAGE_DATA_SELECTOR).stream().findAny()
                .map(Element::data)
                .map(content -> {
                    try {
                        return mapper.readValue(content, RozetkaData.class);
                    } catch (IOException e) {
                        return new RozetkaData();
                    }
                })
                .map(data -> {
                    var characteristics = getCharacteristics(document);

                    return ProductInfo.builder()
                            .name(data.getName())
                            .imageUrls(data.getImage())
                            .ean(data.getEan())
                            .description(data.getDescription())
                            .price(data.getOffers().getPrice())
                            .brand(data.getBrand().getName())
                            .country(characteristics.getOrDefault(CHARACTERISTIC_COUNTRY, null))
                            .volume(Optional.ofNullable(characteristics.getOrDefault(CHARACTERISTIC_VOLUME, null))
                                    .map(VolumeUtils::parse)
                                    .orElse(null))
                            .abv(Optional.ofNullable(characteristics.getOrDefault(CHARACTERISTIC_ABV, null))
                                    .map(AlcoholByVolumeUtil::parse)
                                    .orElse(null)
                            )
                            .rating(data.getAggregateRating() != null ? data.getAggregateRating().getRatingValue() : null)
                            .votes(data.getAggregateRating() != null ? data.getAggregateRating().getRatingCount() : null)
                            .build();
                })
                .orElse(null);
    }

    private Map<String, String> getCharacteristics(Document document) {
        return Optional.ofNullable(document.selectFirst("#rz-client-state"))
                .map(block -> block.data()
                        .replace("&q;", "\"")
                        .replace("&s;", "'"))
                .map(block -> {
                    try {
                        var tree = mapper.readTree(block);
                        return tree.properties().stream()
                                .filter(entry -> entry.getKey().contains("get-characteristic"))
                                .map(Map.Entry::getValue)
                                .map(node -> node.get("body").get("data"))
                                .map(node -> (ArrayNode) node)
                                .flatMap(arrayNode -> StreamSupport.stream(
                                        Spliterators.spliteratorUnknownSize(arrayNode.elements(), Spliterator.ORDERED), false))
                                .map(object -> (ArrayNode) object.get("options"))
                                .flatMap(options -> StreamSupport.stream(
                                        Spliterators.spliteratorUnknownSize(options.elements(), Spliterator.ORDERED), false))
                                .collect(Collectors.toMap(
                                        node -> node.get("title").asText(),
                                        node -> node.get("values").get(0).get("title").asText()));
                    } catch (IOException e) {
                        log.error("Cannot get characteristics", e);
                        return null;
                    }
                })
                .orElse(Map.of());
    }
}
