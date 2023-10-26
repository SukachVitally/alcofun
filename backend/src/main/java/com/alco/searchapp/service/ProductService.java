package com.alco.searchapp.service;

import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.dto.CharacteristicDto;
import com.alco.searchapp.dto.ProductDto;
import com.alco.searchapp.entity.ProductEntity;
import com.alco.searchapp.entity.ProductProviderEntity;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//TODO: Do we need this service? It should be responsible only for product repository
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    private final ImageService imageService;

    private final ProviderService providerService;

    private final CharacteristicService characteristicService;

    @Deprecated
    public Mono<ProductDto> getProduct(Long id) {
        return productRepository.findById(id)
                .flatMap(this::map);
    }

    public Mono<ProductProviderEntity> addProduct(Product product) {
        var providerType = product.getProviderType();
        return productRepository.findOneByEan(product.getInfo().getEan())
                .switchIfEmpty(createProduct(product))
                .flatMap(entity -> {
                    if (entity.getDescription() == null && product.getInfo().getDescription() != null) {
                        entity.setDescription(product.getInfo().getDescription());
                        return productRepository.save(entity);
                    }
                    return Mono.just(entity);
                })
                .flatMap(entity -> providerService.createProviderIfNotExist(providerType.getId())
                        .flatMap(provider -> providerService.addProviderProduct(entity.getId(), provider.getId(),
                                product.getInfo().getPrice(), product.getUrl())));
    }

    private Mono<ProductEntity> createProduct(Product product) {
        return categoryService.createCategoryIfNotExist(product.getCategory())
                .flatMap(category -> providerService.createProviderIfNotExist(product.getProviderType().getId())
                        .flatMap(provider -> {
                            var entity = new ProductEntity();
                            entity.setDescription(product.getInfo().getDescription());
                            entity.setName(product.getInfo().getName());
                            entity.setEan(product.getInfo().getEan());
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
                    if (product.getInfo().getAbv() != null) {
                        map.put(CharacteristicTypeEnum.ALCOHOL_BY_VOLUME, product.getInfo().getAbv().toString());
                    }
                    if (product.getInfo().getVolume() != null) {
                        map.put(CharacteristicTypeEnum.VOLUME, product.getInfo().getVolume().toString());
                    }

                    if (product.getInfo().getCountry() != null) {
                        map.put(CharacteristicTypeEnum.COUNTRY, product.getInfo().getCountry());
                    }

                    if (product.getInfo().getBrand() != null) {
                        map.put(CharacteristicTypeEnum.BRAND, product.getInfo().getBrand());
                    }

                    return Flux.fromStream(map.entrySet().stream())
                            .flatMap(entry -> characteristicService.add(CharacteristicDto.builder()
                                    .type(entry.getKey())
                                    .value(entry.getValue())
                                    .build(), entity.getId()))
                            .then(Mono.just(entity));
                })
                .flatMap(entity -> Flux.fromIterable(product.getInfo().getImageUrls())
                        .concatMap(imageUrl -> imageService.addImage(imageUrl, entity.getId()))
                        .then(Mono.just(entity)));
    }

    @Deprecated
    private Mono<ProductDto> map(ProductEntity entity) {
        Mono<List<CharacteristicDto>> characteristicsMono = characteristicService.getAllForProduct(entity.getId()).collectList();

        return categoryService.getCategory(entity.getCategoryId())
                .flatMap(category -> imageService.getProductImageUrls(entity.getId())
                        .flatMap(imageUrls -> providerService.getRelevantProducts(entity.getId())
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
}
