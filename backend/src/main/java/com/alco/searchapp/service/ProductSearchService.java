package com.alco.searchapp.service;

import com.alco.searchapp.dto.BestPriceDto;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ListResponseDto;
import com.alco.searchapp.entity.ProductEntity;
import com.alco.searchapp.entity.elastic.ProductSearchEntity;
import com.alco.searchapp.exception.ProductNotFoundException;
import com.alco.searchapp.repository.elastic.ProductSearchRepository;
import com.alco.searchapp.repository.ProductRepository;
import com.alco.searchapp.utils.ProductQueryUtils;
import com.alco.searchapp.utils.PageableUtils;
import java.util.List;
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

    private final CharacteristicService characteristicService;

    private final ImageService imageService;

    private final RatingService ratingService;

    private final BestPriceService bestPriceService;

    private final CategoryService categoryService;

    public Mono<List<Long>> refresh() {
        return generateDenormalizedStructure()
                .flatMap(searchRepository::save)
                .map(ProductSearchEntity::getId)
                .collectList();
    }

    public Mono<ProductSearchEntity> getProduct(Long id) {
        return searchRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(id)));
    }

    public Flux<ProductSearchEntity> getAllRelevantProducts() {
        return searchRepository.findAllByPriceNotNull();
    }

    public Mono<ListResponseDto<ProductSearchEntity>> getProducts(Filterable filterable, Pageable pageable) {
        var internalPageable = PageableUtils.mapToInternal(pageable);
        return Mono.just(filterable.getCharacteristics())
                .flatMap(ids -> ids.isEmpty()
                        ? Mono.just(List.<List<Long>>of())
                        : characteristicService.spitByTypes(ids))
                .flatMap(blocks -> template
                        .searchForPage(ProductQueryUtils.createQuery(filterable, blocks).setPageable(internalPageable), ProductSearchEntity.class)
                        .map(result -> ListResponseDto.<ProductSearchEntity>builder()
                                .results(result.getContent().stream()
                                        .map(SearchHit::getContent)
                                        .toList())
                                .pageCount(result.getTotalPages())
                                .currentPage(pageable.getPageNumber())
                                .build()
                        ));
    }

    public Mono<Double> getMinPrice() {
        return getPrice(Sort.Direction.ASC);
    }

    public Mono<Double> getMaxPrice() {
        return getPrice(Sort.Direction.DESC);
    }

    private Mono<Double> getPrice(Sort.Direction direction) {
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder()
                .withPageable(PageRequest.of(0, 1, Sort.by(direction, "price")));

        return template.searchForPage(new NativeQuery(queryBuilder), ProductSearchEntity.class)
                .map(result -> result.getContent().stream()
                        .map(SearchHit::getContent)
                        .findFirst()
                        .map(ProductSearchEntity::getPrice)
                        .orElse(0d));
    }

    private Flux<ProductSearchEntity> generateDenormalizedStructure() {
        return productRepository.findAll()
                .map(this::mapProductToEntity)
                .flatMap(this::addCategory)
                .flatMap(this::addProviders)
                .flatMap(this::addCharacteristics)
                .flatMap(this::addImageInfo)
                .flatMap(this::addRating);
    }

    private Mono<ProductSearchEntity> addCategory(ProductSearchEntity entity) {
        return categoryService.getCategory(entity.getCategoryId()).map(categoryDto -> {
            entity.setCategoryName(categoryDto.getType().getName());
            return entity;
        });
    }

    private Mono<ProductSearchEntity> addProviders(ProductSearchEntity entity) {
        return providerService.getRelevantProducts(entity.getId())
                .flatMap(productProviders ->
                        bestPriceService.getBestPrice(entity.getId(), productProviders)
                                .map(bestPrice -> {
                                    var providers = productProviders.stream()
                                            .map(providerProduct -> ProductSearchEntity.Provider.builder()
                                                    .id(providerProduct.getProviderId())
                                                    .price(providerProduct.getPrice())
                                                    .name(providerProduct.getProviderType().getName())
                                                    .hot(isHot(bestPrice, providerProduct.getPrice()))
                                                    .link(providerProduct.getLink())
                                                    .build())
                                            .toList();
                                    entity.setProviders(providers);
                                    entity.setPrice(bestPrice.getPrice());
                                    entity.setPercent(bestPrice.getPercent());
                                    entity.setHot(bestPrice.getHot());
                                    return entity;
                                })
                                .onErrorReturn(entity));
    }

    private Mono<ProductSearchEntity> addCharacteristics(ProductSearchEntity entity) {
        return characteristicService.getAllForProduct(entity.getId())
                .collectList()
                .map(list -> list.stream()
                        .map(dto -> ProductSearchEntity.Characteristic.builder()
                                .id(dto.getId())
                                .name(dto.getType().getName())
                                .unitOfMeasurement(dto.getType().getUnitOfMeasurement())
                                .value(dto.getValue())
                                .build()
                        )
                        .toList())
                .map(list -> {
                    entity.setCharacteristics(list);
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
                    entity.setCompositeRating(compositeRating.rating());
                    entity.setVotes(compositeRating.votes());
                    entity.setInternalRating(ratingService.getInternalRating(compositeRating));

                    return entity;
                });
    }

    private Boolean isHot(BestPriceDto bestPrice, Double price) {
        return bestPrice.getHot() && bestPrice.getPrice().equals(price);
    }

    private ProductSearchEntity mapProductToEntity(ProductEntity product) {
        var entity = new ProductSearchEntity();
        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setEan(product.getEan());
        entity.setCategoryId(product.getCategoryId());
        return entity;
    }
}
