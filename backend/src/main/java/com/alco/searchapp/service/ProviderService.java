package com.alco.searchapp.service;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.ProviderDto;
import com.alco.searchapp.dto.ProviderProductDto;
import com.alco.searchapp.entity.ProviderEntity;
import com.alco.searchapp.entity.ProductProviderEntity;
import com.alco.searchapp.repository.ProductProviderRepository;
import com.alco.searchapp.repository.ProviderRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProviderService {
    private static final int DAY_INTERVAL = 1;

    private final ProductProviderRepository productProviderRepository;

    private final ProviderRepository providerRepository;

    public Mono<ProviderEntity> createProviderIfNotExist(Integer typeId) {
        return providerRepository.findOneByTypeId(typeId)
                .switchIfEmpty(createProvider(typeId));
    }

    public Flux<ProviderDto> getProviders() {
        return providerRepository.findAll()
                .map(entity -> ProviderDto.builder()
                        .id(entity.getId())
                        .type(ProviderTypeEnum.findById(entity.getTypeId()))
                        .build())
                .sort(Comparator.comparing(providerDto -> providerDto.getType().getName()));
    }

    public Mono<ProductProviderEntity> addProviderProduct(Long productId, Long providerId, Double price, String link ) {
        var providerProductEntity = new ProductProviderEntity();
        providerProductEntity.setProductId(productId);
        providerProductEntity.setProviderId(providerId);
        providerProductEntity.setPrice(price);
        providerProductEntity.setLink(link);
        providerProductEntity.setCreatedAt(LocalDateTime.now());

        return productProviderRepository.save(providerProductEntity);
    }

    public Flux<ProviderProductDto> getRelevantProducts(Long id) {
        return findRelevantProducts(id)
                .flatMap(entity -> providerRepository.findById(entity.getProviderId())
                        .map(provider -> ProviderProductDto.builder()
                                .providerId(provider.getId())
                                .providerType(ProviderTypeEnum.findById(provider.getTypeId()))
                                .price(entity.getPrice())
                                .link(entity.getLink())
                                .createdAt(entity.getCreatedAt())
                                .build()));
    }

    private Flux<ProductProviderEntity> findRelevantProducts(Long id) {
        var date = LocalDateTime.now().minusDays(DAY_INTERVAL); // todo: Configure time interval

        return productProviderRepository.findByProductIdAndCreatedAtAfter(id, date);
    }

    private Mono<ProviderEntity> createProvider(Integer typeId) {
        var entity = new ProviderEntity();
        entity.setTypeId(typeId);
        entity.setCreatedAt(LocalDateTime.now());
        return providerRepository.save(entity)
                .onErrorResume(DuplicateKeyException.class, e -> providerRepository.findOneByTypeId(typeId));
    }
}
