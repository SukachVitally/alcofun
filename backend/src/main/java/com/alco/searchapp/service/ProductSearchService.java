package com.alco.searchapp.service;

import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.productlist.ProductInfoDto;
import com.alco.searchapp.entity.DenormalizedProductEntity;
import com.alco.searchapp.repository.CustomProductRepository;
import com.alco.searchapp.repository.DenormalizedProductRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
@Slf4j
public class ProductSearchService {
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
        Query query = null;
        return template.search(query, DenormalizedProductEntity.class).map(SearchHit::getContent)
            .map(dto -> {
                var price = dto.getProviders().entrySet().stream()
                        .filter(entry -> filterable.getProviders() == null
                                || filterable.getProviders().isEmpty()
                                || filterable.getProviders().contains(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .min(Comparator.comparingDouble(value -> value))
                        .orElse(null);

                return ProductInfoDto.builder()
                        .id(dto.getId())
                        .ean(dto.getEan())
                        .name(dto.getName())
                        .categoryId(dto.getCategoryId())
                        .characteristicIds(dto.getCharacteristicIds())
                        .price(price)
                        .build();
            });
    }
}
