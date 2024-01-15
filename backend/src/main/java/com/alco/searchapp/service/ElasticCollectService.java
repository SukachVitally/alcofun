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
                .then(refreshCategories())
                .then(refreshCharacteristics())
                .then(getRelevantProductIds());
    }

    private Mono<Void> refreshCategories() {
        return productSearchService.getAllRelevantProducts()
                .map(ProductSearchEntity::getCategoryId)
                .distinct()
                .collectList()
                .flatMap(categoryService::refresh)
                .then();
    }

    private Mono<Void> refreshCharacteristics() {
        return productSearchService.getAllRelevantProducts()
                .flatMap(entity -> Flux.fromIterable(entity.getCharacteristics()))
                .map(ProductSearchEntity.Characteristic::getId)
                .distinct()
                .collectList()
                .flatMap(characteristicService::refresh)
                .then();
    }

    private Mono<List<Long>> getRelevantProductIds() {
        return productSearchService.getAllRelevantProducts()
                .map(ProductSearchEntity::getId)
                .collectList();
    }

}
