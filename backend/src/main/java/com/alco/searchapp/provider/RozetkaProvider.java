package com.alco.searchapp.provider;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.RozetkaProperties;
import com.alco.searchapp.provider.dto.ProductInfo;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.alco.searchapp.provider.dto.rozetka.RozetkaData;
import com.alco.searchapp.utils.AlcoholByVolumeUtil;
import com.alco.searchapp.utils.VolumeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class RozetkaProvider implements IProvider  {
    private static final String LIST_LINK_SELECTOR = "a.goods-tile__picture";

    private static final String PAGE_DATA_SELECTOR = "script[data-seo=Product]";

    private static final String CHARACTERISTIC_VOLUME = "Об'єм";
    private static final String CHARACTERISTIC_ABV = "Міцність";
    private static final String CHARACTERISTIC_COUNTRY = "Країна";

    private final ObjectMapper mapper;

    private final HtmlLinkParser linkParser;

    private final RozetkaProperties properties;

    public Function<Integer, String> createLinkFunction(String url) {
        return (Integer page) -> String.format("%spage=%s;sell_status=available,limited,out_of_stock/", url, page);
    }

    @Override
    public Flux<ProductPageDto> getProductPages() {
        return Flux.fromIterable(properties.getBlocks())
                .flatMap(block -> Flux
                        .fromIterable(new ProductLinkCollector(createLinkFunction(block.getUrl()), LIST_LINK_SELECTOR))
                        .flatMap(Flux::fromIterable)
                        .map(url -> ProductPageDto.builder()
                                .url(url)
                                .provider(getType())
                                .category(block.getType())
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
                            .name(data.getName().replaceAll("\\(.*?\\)", "").trim())
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
                                        node -> node.get("values").get(0).get("title").asText(),
                                        (existValue, newValue) -> existValue
                                ));
                    } catch (IOException e) {
                        log.error("Cannot get characteristics", e);
                        return null;
                    }
                })
                .orElse(Map.of());
    }
}
