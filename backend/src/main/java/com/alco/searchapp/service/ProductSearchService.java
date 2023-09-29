package com.alco.searchapp.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ListResponseDto;
import com.alco.searchapp.dto.productlist.ProductInfoDto;
import com.alco.searchapp.entity.DenormalizedProductEntity;
import com.alco.searchapp.entity.ProductCharacteristicEntity;
import com.alco.searchapp.entity.ProviderProductEntity;
import com.alco.searchapp.repository.DenormalizedProductRepository;
import com.alco.searchapp.repository.ProductCharacteristicRepository;
import com.alco.searchapp.repository.ProductRepository;
import com.alco.searchapp.repository.ProviderProductRepository;
import com.alco.searchapp.utils.ProductQueryUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
@Slf4j
public class ProductSearchService {
    private final DenormalizedProductRepository searchRepository;

    private final ProviderProductRepository providerProductRepository;

    private final ProductRepository productRepository;

    private final ReactiveElasticsearchTemplate template;

    private final ProductCharacteristicRepository productCharacteristicRepository;

    private final CharacteristicService characteristicService;

    private final ImageService imageService;

    private final Map<String, String> SORT_PROPERTIES_MAP = Map.of("name", "name.keyword",
            "created_at", "id");

    public Mono<Boolean> refresh() {
        var date = LocalDateTime.now().minusDays(1);
        return searchRepository.deleteAll()
                .log()
                .then(generateDenormalizedStructure(date)
                        .flatMap(searchRepository::save)
                        .log()
                        .then(Mono.just(true)));
    }

    public Mono<ListResponseDto<ProductInfoDto>> getProducts(Filterable filterable, Pageable pageable) {
        var internalPageable = mapToInternal(pageable);
        return Mono.just(filterable.getCharacteristics())
                .flatMap(ids -> ids.isEmpty()
                        ? Mono.just(List.<List<Long>>of())
                        : characteristicService.spitByTypes(ids))
                .flatMap(blocks -> template
                        .searchForPage(createQuery(filterable, blocks).setPageable(internalPageable), DenormalizedProductEntity.class)
                        .map(result -> ListResponseDto.<ProductInfoDto>builder()
                                .results(result.getContent().stream()
                                        .map(SearchHit::getContent)
                                        .map(this.map(filterable.getProviders()))
                                        .toList())
                                .pageCount(result.getTotalPages())
                                .currentPage(pageable.getPageNumber())
                                .build()
                        ));
    }

    private Flux<DenormalizedProductEntity> generateDenormalizedStructure(LocalDateTime time) {
        return providerProductRepository.findAllByCreatedAtAfterOrderByIdDesc(time)
            .groupBy(ProviderProductEntity::getProductId)
            .flatMap(providerProductGroup -> providerProductGroup.collectList()
                .flatMap(productProviders ->
                    productCharacteristicRepository.findAllByProductId(providerProductGroup.key())
                        .collectList()
                        .map(list -> list.stream().map(ProductCharacteristicEntity::getCharacteristicId).toList())
                        .flatMap(characteristicIds -> imageService.getProductMainImageUrl(providerProductGroup.key())
                            .flatMap(imageUrl -> productRepository.findById(providerProductGroup.key())
                                .map(product -> {
                                    var entity = new DenormalizedProductEntity();
                                    entity.setId(product.getId());
                                    entity.setEan(product.getEan());
                                    entity.setName(product.getName());
                                    entity.setCategoryId(product.getCategoryId());
                                    entity.setImageUrl(imageUrl);
                                    entity.setCharacteristicIds(characteristicIds);
                                    entity.setProviders(productProviders.stream()
                                        .map(providerProduct -> new DenormalizedProductEntity.Provider(
                                                providerProduct.getProviderId(),
                                                providerProduct.getPrice()))
                                        .toList());
                                    return entity;
                                })))
                ));
    }

    private NativeQuery createQuery(Filterable filterable, List<List<Long>> characteristicBlocks) {
        NativeQueryBuilder builder = new NativeQueryBuilder();
        ArrayList<Query> queries = new ArrayList<>();

        if (!filterable.getCategories().isEmpty()) {
            queries.add(ProductQueryUtils.createCategoriesQuery(filterable.getCategories()));
        }

        if (filterable.getSearch() != null) {
            queries.add(ProductQueryUtils.createSearchQuery(filterable.getSearch()));
        }

        if (!characteristicBlocks.isEmpty()) {
            queries.add(ProductQueryUtils.createCharacteristicsQuery(characteristicBlocks));
        }

        if (!filterable.getProviders().isEmpty()
                || filterable.getPrice().min() != null
                || filterable.getPrice().max() != null) {
            queries.add(ProductQueryUtils.createProviderQuery(filterable.getProviders(), filterable.getPrice()));
        }

        builder.withFilter(QueryBuilders.bool(b -> b.filter(queries)));

        return new NativeQuery(builder);
    }

    private Function<DenormalizedProductEntity, ProductInfoDto> map(List<Long> providers) {
        return entity -> {
            var price = entity.getProviders().stream()
                    .filter(provider -> providers.isEmpty() || providers.contains(provider.getId()))
                    .map(DenormalizedProductEntity.Provider::getPrice)
                    .min(Comparator.comparingDouble(value -> value))
                    .orElse(null);

            return ProductInfoDto.builder()
                    .id(entity.getId())
                    .ean(entity.getEan())
                    .name(entity.getName())
                    .categoryId(entity.getCategoryId())
                    .characteristicIds(entity.getCharacteristicIds())
                    .price(price)
                    .build();
        };
    }

    private Pageable mapToInternal(Pageable pageable) {
        var sort = Sort.by(pageable.getSort().stream()
                    .filter(order -> SORT_PROPERTIES_MAP.containsKey(order.getProperty()))
                    .map(order -> new Sort.Order(order.getDirection(), SORT_PROPERTIES_MAP.get(order.getProperty())))
                    .toList());
        return PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);
    }
}
