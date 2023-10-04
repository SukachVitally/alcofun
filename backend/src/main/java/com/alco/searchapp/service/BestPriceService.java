package com.alco.searchapp.service;

import com.alco.searchapp.dto.BestPriceDto;
import com.alco.searchapp.dto.ProductProviderDto;
import com.alco.searchapp.entity.BestPriceEntity;
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
    // todo: Inject from properties
    public static Double minPercent = 10d;

    private final BestPriceRepository repository;

    public Mono<BestPriceDto> getBestPrice(Long productId, List<ProductProviderDto> productProviders) {
        var bestProvider = productProviders.stream()
                .min(Comparator.comparingDouble(ProductProviderDto::getPrice))
                .orElse(null);
        if (bestProvider == null) {
            return Mono.empty();
        }

        return repository.findByProductId(productId)
                .defaultIfEmpty(createNewEntity(productId, bestProvider))
                .flatMap(entity -> {
                    if ((entity.getPrice().equals(bestProvider.getPrice()) && entity.getHot())) {
                        return Mono.just(entity);
                    }

                    Double percent = Math.ceil((entity.getPrice() - bestProvider.getPrice()) / entity.getPrice() * 100);

                    entity.setHot(percent > minPercent);
                    entity.setPercent(percent);
                    entity.setPrice(bestProvider.getPrice());
                    entity.setUpdatedAt(LocalDateTime.now());

                    return repository.save(entity);
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
        entity.setHot(false);

        return entity;
    }
}
