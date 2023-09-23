package com.alco.searchapp.service;

import com.alco.searchapp.entity.DenormalizedProductEntity;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.productlist.ProductInfoDto;
import com.alco.searchapp.repository.CustomProductRepository;
import com.alco.searchapp.repository.DenormalizedProductRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
@Slf4j
public class ProductSearchService {

    private final CustomProductRepository repository;

    private final DenormalizedProductRepository denormalizedProductRepository;

    public Mono<Boolean> refresh() {
        return denormalizedProductRepository.clean()
                .flatMap(unused -> {
                    var date = LocalDateTime.now().minusDays(1);
                    return repository.generateDenormalizedStructure(date)
                            .collectList()
                            .flatMap(denormalizedProductRepository::addList);
                })
                .then(Mono.just(true));
    }

    public Flux<ProductInfoDto> getProducts(Filterable filterable) {
        return Optional.ofNullable(filterable.getCharacteristics())
                .filter(characteristics -> !characteristics.isEmpty())
                .map(characteristics -> repository.getCharacteristicBlocks(characteristics)
                        .collectList())
                .orElse(Mono.just(List.of()))
                .flatMapMany(characteristicBlocks -> getDenormalizedStructure()
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
                        })
                        .filter(dto -> dto.getPrice() != null)
                        .filter(this.filter(filterable, characteristicBlocks)));
    }

    private Flux<DenormalizedProductEntity> getDenormalizedStructure() {
        var date = LocalDateTime.now().minusDays(1);
        return repository.generateDenormalizedStructure(date);
    }

    private Predicate<ProductInfoDto> filter(Filterable filterable, List<List<Long>> characteristicBlocks) {
        return dto ->  filterCategories(dto, filterable.getCategories())
                    && filterSearch(dto, filterable.getSearch())
                    && filterPrice(dto, filterable.getPrice())
                    && filterCharacteristics(dto, characteristicBlocks);

    }

    private Boolean filterCategories(ProductInfoDto dto, List<Long> categories) {
        return categories == null
                || categories.isEmpty()
                || categories.contains(dto.getCategoryId());
    }

    private Boolean filterSearch(ProductInfoDto dto, String search) {
        return search == null
                || search.isEmpty()
                || dto.getName().toLowerCase().contains(search.toLowerCase())
                || dto.getEan().equals(search);
    }

    private Boolean filterPrice(ProductInfoDto dto, Filterable.Price price) {
        if (price == null || (price.min() == null && price.max() == null)) {
            return true;
        }

        return (price.min() == null || dto.getPrice() >= price.min())
                && (price.max() == null || dto.getPrice() <= price.max());
    }

    private Boolean filterCharacteristics(ProductInfoDto dto, List<List<Long>> blocks) {
        if (blocks.isEmpty()) {
            return true;
        }
        return blocks.stream().allMatch(list -> list.stream()
                .anyMatch(item -> dto.getCharacteristicIds().contains(item)));
    }
}
