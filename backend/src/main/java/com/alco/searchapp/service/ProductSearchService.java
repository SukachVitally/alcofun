package com.alco.searchapp.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.alco.searchapp.dto.BestPriceDto;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ListResponseDto;
import com.alco.searchapp.entity.ProductSearchEntity;
import com.alco.searchapp.entity.ProductCharacteristicEntity;
import com.alco.searchapp.repository.ProductSearchRepository;
import com.alco.searchapp.repository.ProductCharacteristicRepository;
import com.alco.searchapp.repository.ProductRepository;
import com.alco.searchapp.utils.ProductQueryUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final ProductSearchRepository searchRepository;

    private final ProviderService providerService;

    private final ProductRepository productRepository;

    private final ReactiveElasticsearchTemplate template;

    private final ProductCharacteristicRepository productCharacteristicRepository;

    private final CharacteristicService characteristicService;

    private final ImageService imageService;

    private final RatingService ratingService;

    private final BestPriceService bestPriceService;

    private final Map<String, String> SORT_PROPERTIES_MAP = Map.of("name", "name.keyword",
            "created_at", "id",
            "rating", "internalRating"
//            "hot", "providers.percent" // todo: Add hot search sorting
    );

    public Mono<Long> refresh() {
        return searchRepository.deleteAll()
                .thenMany(generateDenormalizedStructure())
                .flatMap(searchRepository::save)
                .count();
    }

    public Mono<ListResponseDto<ProductSearchEntity>> getProducts(Filterable filterable, Pageable pageable) {
        var internalPageable = mapToInternal(pageable);
        return Mono.just(filterable.getCharacteristics())
                .flatMap(ids -> ids.isEmpty()
                        ? Mono.just(List.<List<Long>>of())
                        : characteristicService.spitByTypes(ids))
                .flatMap(blocks -> template
                        .searchForPage(createQuery(filterable, blocks).setPageable(internalPageable), ProductSearchEntity.class)
                        .map(result -> ListResponseDto.<ProductSearchEntity>builder()
                                .results(result.getContent().stream()
                                        .map(SearchHit::getContent)
                                        .toList())
                                .pageCount(result.getTotalPages())
                                .currentPage(pageable.getPageNumber())
                                .build()
                        ));
    }

    private Flux<ProductSearchEntity> generateDenormalizedStructure() {
        return productRepository.findAll()
                .map(product -> {
                        var entity = new ProductSearchEntity();
                        entity.setId(product.getId());
                        entity.setName(product.getName());
                        entity.setDescription(product.getDescription());
                        entity.setEan(product.getEan());
                        entity.setCategoryId(product.getCategoryId());
                        return entity;
                })
                .flatMap(this::addProviders)
                .flatMap(this::addCharacteristics)
                .flatMap(this::addImageInfo)
                .flatMap(this::addRating);
    }

    private Mono<ProductSearchEntity> addProviders(ProductSearchEntity entity) {
        return providerService.getRelevantProducts(entity.getId())
                .flatMap(productProviders ->
                        bestPriceService.getBestPrice(entity.getId(), productProviders)
                                .map(bestPrice ->
                                        productProviders.stream()
                                                .map(providerProduct -> ProductSearchEntity.Provider.builder()
                                                        .id(providerProduct.getProviderId())
                                                        .price(providerProduct.getPrice())
                                                        .hot(isHot(bestPrice, providerProduct.getPrice()))
                                                        .percent(isHot(bestPrice, providerProduct.getPrice()) ? bestPrice.getPercent() : null)
                                                        .build())
                                                .toList()))
                .map(list -> {
                    entity.setProviders(list);
                    return entity;
                });
    }

    private Mono<ProductSearchEntity> addCharacteristics(ProductSearchEntity entity) {
        return productCharacteristicRepository.findAllByProductId(entity.getId())
                .collectList()
                .map(list -> list.stream().map(ProductCharacteristicEntity::getCharacteristicId).toList())
                .map(ids -> {
                    entity.setCharacteristicIds(ids);
                    return entity;
                });
    }

    private Mono<ProductSearchEntity> addImageInfo(ProductSearchEntity entity) {
        return imageService.getProductImageUrls(entity.getId())
                .map(urls -> {
                    entity.setImageUrls(urls);
                    return entity;
                });
    }

    private Mono<ProductSearchEntity> addRating(ProductSearchEntity entity) {
        return ratingService.getCompositeRating(entity.getId())
                .map(compositeRating -> {
                    entity.setRating(compositeRating);
                    entity.setInternalRating(ratingService.getInternalRating(compositeRating));

                    return entity;
                });
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
                || filterable.getPrice().max() != null
                || filterable.getHot()) {
            queries.add(ProductQueryUtils.createProviderQuery(filterable.getProviders(), filterable.getPrice(),
                    filterable.getHot()));
        }

        builder.withFilter(QueryBuilders.bool(b -> b.filter(queries)));

        return new NativeQuery(builder);
    }
    
    private Pageable mapToInternal(Pageable pageable) {
        var sort = Sort.by(pageable.getSort().stream()
                    .filter(order -> SORT_PROPERTIES_MAP.containsKey(order.getProperty()))
                    .map(order -> new Sort.Order(order.getDirection(), SORT_PROPERTIES_MAP.get(order.getProperty())))
                    .toList());
        return PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);
    }

    private Boolean isHot(BestPriceDto bestPrice, Double price) {
        return bestPrice.getHot() && bestPrice.getPrice().equals(price);
    }
}
