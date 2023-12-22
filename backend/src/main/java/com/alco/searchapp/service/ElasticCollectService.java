package com.alco.searchapp.service;

import com.alco.searchapp.entity.elastic.ProductSearchEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ElasticCollectService {
    private final ProductSearchService productSearchService;

    private final CharacteristicService characteristicService;

    private final CategoryService categoryService;

    public Mono<List<Long>> refresh() {
        return productSearchService.refresh()
                .flatMap(ids -> productSearchService.getAllProducts()
                    .map(ProductSearchEntity::getCategoryId)
                    .distinct()
                    .collectList()
                    .flatMap(categoryService::refresh))
                .flatMap(ids -> productSearchService.getAllProducts()
                    .flatMap(entity -> Flux.fromIterable(entity.getCharacteristics().stream()
                            .map(ProductSearchEntity.Characteristic::getId)
                            .toList()))
                    .distinct()
                    .collectList()
                    .flatMap(characteristicService::refresh));
    }
}
