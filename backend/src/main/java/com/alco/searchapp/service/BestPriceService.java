package com.alco.searchapp.service;

import com.alco.searchapp.dto.BestPriceDto;
import com.alco.searchapp.dto.ProductProviderDto;
import com.alco.searchapp.entity.BestPriceEntity;
import com.alco.searchapp.properites.BestPriceProperties;
import com.alco.searchapp.repository.BestPriceRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BestPriceService {
    private final BestPriceRepository repository;

    private final BestPriceProperties properties;

    // todo: Separate method on update and get best price.
    public Mono<BestPriceDto> getBestPrice(Long productId, List<ProductProviderDto> productProviders) {
        var bestProvider = productProviders.stream()
                .min(Comparator.comparingDouble(ProductProviderDto::getPrice))
                .orElse(null);
        if (bestProvider == null) {
            return Mono.empty();
        }

        return repository.findFirstByProductIdOrderByIdDesc(productId)
                .switchIfEmpty(repository.save(createNewEntity(productId, bestProvider)))
                .flatMap(entity -> {
                    if ((entity.getPrice().equals(bestProvider.getPrice()))) {
                        return Mono.just(entity);
                    }

                    Double percent = Math.ceil((entity.getPrice() - bestProvider.getPrice()) / entity.getPrice() * 100);

                    var newEntity = createNewEntity(productId, bestProvider);
                    newEntity.setHot(percent > properties.getMinPercent());
                    newEntity.setPercent(percent);

                    return repository.save(newEntity);
                })
                .map(entity -> BestPriceDto.builder()
                        .price(entity.getPrice())
                        .hot(entity.getHot())
                        .percent(entity.getPercent())
                        .build());
    }

    private BestPriceEntity createNewEntity(Long productId, ProductProviderDto productProvider) {
        var entity = new BestPriceEntity();
        entity.setCreatedAt(LocalDateTime.now());
        entity.setPrice(productProvider.getPrice());
        entity.setProviderId(productProvider.getProviderId());
        entity.setProductId(productId);
        entity.setPercent(0d);
        entity.setHot(false);

        return entity;
    }
}
