package com.alco.searchapp.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.productlist.ProductInfoDto;
import com.alco.searchapp.entity.DenormalizedProductEntity;
import com.alco.searchapp.repository.CustomProductRepository;
import com.alco.searchapp.repository.DenormalizedProductRepository;
import com.alco.searchapp.utils.ProductQueryUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
@Slf4j
public class ProductSearchService {
    private final ReactiveElasticsearchClient client;
    private final CustomProductRepository repository;

    private final DenormalizedProductRepository searchRepository;

    private final ReactiveElasticsearchTemplate template;

    public Mono<Boolean> refresh() {
        var date = LocalDateTime.now().minusDays(1);
        return searchRepository.deleteAll()
                .log()
                .then(repository.generateDenormalizedStructure(date)
                        .flatMap(searchRepository::save)
                        .log()
                        .then(Mono.just(true)));
    }

    public Flux<ProductInfoDto> getProducts(Filterable filterable, Pageable pageable) {
        return Mono.just(filterable.getCharacteristics())
                .flatMap(ids -> ids.isEmpty()
                        ? Mono.just(List.<List<Long>>of())
                        : repository.getCharacteristicBlocks(ids).collectList())
                .flatMapMany(blocks -> template.searchForPage(createQuery(filterable, blocks).setPageable(pageable),
                                DenormalizedProductEntity.class)
                        .flatMapMany(result -> Flux.fromIterable(result.getContent().stream()
                                        .map(SearchHit::getContent)
                                        .toList()))
                        .map(this.map(filterable.getProviders())));
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
}
