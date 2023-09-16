package com.alco.searchapp.service;

import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.dto.CharacteristicDto;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ProductDto;
import com.alco.searchapp.dto.ProductInfoDto;
import com.alco.searchapp.entity.ProductEntity;
import com.alco.searchapp.entity.ProviderProductEntity;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.repository.CustomProductRepository;
import com.alco.searchapp.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    private final ImageService imageService;

    private final ProviderService providerService;

    private final CharacteristicService characteristicService;

    private final CustomProductRepository customProductRepository;

    public Flux<ProductInfoDto> getProducts(Filterable filterable, Pageable pageable) {
        var date = LocalDateTime.now().minusDays(1);
        var lst = customProductRepository.generateDenormalizedStructure(date).collectList().block();
        return customProductRepository.findAll(filterable, pageable, date)
                .flatMap(entity -> mapProductInfo(entity, filterable.getProviders()));
    }

    public Mono<Long> getProductCount(Filterable filter) {
        var date = LocalDateTime.now().minusDays(1);
        return customProductRepository.count(filter, date);
    }

    public Mono<ProductDto> getProduct(Long id) {
        return productRepository.findById(id)
                .flatMap(this::map);
    }

    public Mono<ProviderProductEntity> addProductInfo(Product product) {
        var providerType = product.getProviderType();
        return productRepository.findOneByEan(product.getEan())
                .switchIfEmpty(createProduct(product))
                .flatMap(entity -> providerService.createProviderIfNotExist(providerType.getId())
                        .flatMap(provider -> providerService.addProviderProduct(entity.getId(), provider.getId(),
                                product.getPrice(), product.getLink())));
    }

    private Mono<ProductEntity> createProduct(Product product) {
        return categoryService.createCategoryIfNotExist(product.getCategory())
                .flatMap(category -> providerService.createProviderIfNotExist(product.getProviderType().getId())
                        .flatMap(provider -> {
                            var entity = new ProductEntity();
                            entity.setDescription(product.getDescription());
                            entity.setName(product.getName());
                            entity.setEan(product.getEan());
                            entity.setCreatedAt(LocalDateTime.now());
                            entity.setCategoryId(category.getId());
                            entity.setProviderId(provider.getId());

                            return productRepository.save(entity)
                                    .onErrorResume(DuplicateKeyException.class,
                                            e -> productRepository.findOneByEan(entity.getEan()));
                        })
                )
                .flatMap(entity -> {
                    Map<CharacteristicTypeEnum, String> map = new HashMap<>();
                    if (product.getAbv() != null) {
                        map.put(CharacteristicTypeEnum.ALCOHOL_BY_VOLUME, product.getAbv().toString());
                    }
                    if (product.getVolume() != null) {
                        map.put(CharacteristicTypeEnum.VOLUME, product.getVolume().toString());
                    }

                    if (product.getCountry() != null) {
                        map.put(CharacteristicTypeEnum.COUNTRY, product.getCountry());
                    }

                    if (product.getBrand() != null) {
                        map.put(CharacteristicTypeEnum.BRAND, product.getBrand());
                    }

                    return Flux.fromStream(map.entrySet().stream())
                            .flatMap(entry -> characteristicService.add(CharacteristicDto.<String>builder()
                                    .type(entry.getKey())
                                    .value(entry.getValue())
                                    .build(), entity.getId()))
                            .then(Mono.just(entity));
                })
                .flatMap(entity -> Flux.fromIterable(product.getImageUrls())
                        .flatMap(imageUrl -> imageService.addImage(imageUrl, entity.getId()))
                        .then(Mono.just(entity)));
    }

    private Mono<ProductDto> map(ProductEntity entity) {
        Mono<List<CharacteristicDto>> characteristicsMono = characteristicService.getAllForProduct(entity.getId()).collectList();

        return categoryService.getCategory(entity.getCategoryId())
                .flatMap(category -> imageService.getProductImageUrls(entity.getId())
                        .collectList()
                        .flatMap(imageUrls -> providerService.getRelevantProducts(entity.getId()).collectList()
                                .flatMap(providers -> characteristicsMono
                                        .map(characteristics -> ProductDto.builder()
                                                .category(category.getType())
                                                .id(entity.getId())
                                                .ean(entity.getEan())
                                                .name(entity.getName())
                                                .description(entity.getDescription())
                                                .imageUrls(imageUrls)
                                                .providers(providers)
                                                .characteristics(characteristics)
                                                .build()
                                        )
                                )
                        )
                );
    }

    private Mono<ProductInfoDto> mapProductInfo(ProductEntity entity, List<Long> providerIds) {
        return imageService.getProductImageUrls(entity.getId())
                .collectList()
                .flatMap(imageUrls -> providerService.getRelevantProductPrices(entity.getId())
                        .map(productPrices -> ProductInfoDto.builder()
                                .id(entity.getId())
                                .name(entity.getName())
                                .imageUrl(imageUrls.isEmpty() ? null : imageUrls.get(0))
                                .price(productPrices.entrySet().stream()
                                        .filter(entry -> providerIds == null
                                                || providerIds.isEmpty() || providerIds.contains(entry.getKey()))
                                        .map(Map.Entry::getValue)
                                        .min(Double::compare)
                                        .orElse(null)
                                )
                                .build()));
    }
}
