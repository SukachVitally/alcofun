package com.alco.searchapp.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.productlist.ProductInfoDto;
import com.alco.searchapp.entity.DenormalizedProductEntity;
import com.alco.searchapp.repository.CustomProductRepository;
import com.alco.searchapp.repository.DenormalizedProductRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Flux<ProductInfoDto> getProducts(Filterable filterable) {
        return Mono.just(filterable.getCharacteristics())
                .flatMap(ids -> ids.isEmpty()
                        ? Mono.just(List.<List<Long>>of())
                        : repository.getCharacteristicBlocks(ids).collectList())
                .flatMapMany(blocks -> template.search(createQuery(filterable, blocks),
                                DenormalizedProductEntity.class)
                        .map(SearchHit::getContent)
                        .map(this.map(filterable.getProviders())));
    }

    private NativeQuery createQuery(Filterable filterable, List<List<Long>> characteristicBlocks) {
        NativeQueryBuilder builder = new NativeQueryBuilder();
        var boolQueryBuilder =  QueryBuilders.bool();
        if (!filterable.getCategories().isEmpty()) {
            var values = filterable.getCategories().stream().map(FieldValue::of).toList();

            boolQueryBuilder.filter(f -> f
                    .terms(t -> t
                            .field("categoryId")
                            .terms(tt -> tt.value(values))
                    )
            );
        }

        if (filterable.getSearch() != null) {
            boolQueryBuilder.filter(f -> f.bool(b -> b.should(List.of(
                    new Query.Builder().term(m -> m.field("ean").value(filterable.getSearch())).build(),
                    new Query.Builder().match(p -> p.field("name")
                            .query(filterable.getSearch())
                            .operator(Operator.And)
                        ).build()
            ))));
        }

        if (!characteristicBlocks.isEmpty()) {
            characteristicBlocks.forEach(ids -> boolQueryBuilder.filter(f -> f
                    .terms(t -> t
                            .field("characteristicIds")
                            .terms(tt -> tt.value(ids.stream().map(FieldValue::of).toList()))
                    ))
            );
        }

        if (!filterable.getProviders().isEmpty()) {
            boolQueryBuilder.filter(f -> f.bool(b -> b.should(filterable.getProviders().stream()
                    .map(id -> new Query.Builder()
                            .exists(e -> e.field(String.format("providers.%s", id)))
                            .build())
                    .toList()
            )));
        }

        if (!filterable.getCategories().isEmpty()
                || filterable.getSearch() != null
                || !characteristicBlocks.isEmpty()
                || !filterable.getProviders().isEmpty()) {
            builder.withFilter(QueryBuilders.bool(b -> boolQueryBuilder));
        }



        return new NativeQuery(builder);
    }

    private Function<DenormalizedProductEntity, ProductInfoDto> map(List<Long> providers) {
        return entity -> {
            var price = entity.getProviders().entrySet().stream()
                    .filter(entry -> providers.isEmpty() || providers.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
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
