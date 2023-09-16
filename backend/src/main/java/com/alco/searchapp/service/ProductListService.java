package com.alco.searchapp.service;

import com.alco.searchapp.dto.productlist.DenormalizedProductDto;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.repository.CustomProductRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Service
public class ProductListService {

    private final CustomProductRepository repository;

    public Flux<DenormalizedProductDto> getProducts(Filterable filterable) {
        return Optional.ofNullable(filterable.getCharacteristics())
                .filter(characteristics -> !characteristics.isEmpty())
                .map(repository::getCharacteristicBlocks)
                .map(Flux::collectList)
                .orElse(Mono.empty())
                .flatMapMany(characteristicBlocks -> getDenormalizedStructure()
                        .map(dto -> DenormalizedProductDto.builder()
                                .id(dto.getId())
                                .ean(dto.getEan())
                                .name(dto.getName())
                                .categoryId(dto.getCategoryId())
                                .characteristicIds(dto.getCharacteristicIds())
                                .providers(dto.getProviders().entrySet().stream()
                                        .filter(entry -> filterable.getProviders() == null
                                                || filterable.getProviders().isEmpty()
                                                || filterable.getProviders().contains(entry.getKey()))
                                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                                .build()
                        )
                        .filter(dto -> !dto.getProviders().keySet().isEmpty())
                        .filter(this.filter(filterable, characteristicBlocks)));
    }

    private Flux<DenormalizedProductDto> getDenormalizedStructure() {
        var date = LocalDateTime.now().minusDays(1);
        return repository.generateDenormalizedStructure(date);
    }

    private Predicate<DenormalizedProductDto> filter(Filterable filterable, List<List<Long>> characteristicBlocks) {
        return dto ->  filterCategories(dto, filterable.getCategories())
                    && filterSearch(dto, filterable.getSearch())
                    && filterPrice(dto, filterable.getPrice())
                    && filterCharacteristics(dto, characteristicBlocks);

    }

    private Boolean filterCategories(DenormalizedProductDto dto, List<Long> categories) {
        return categories == null
                || categories.isEmpty()
                || categories.contains(dto.getCategoryId());
    }

    private Boolean filterSearch(DenormalizedProductDto dto, String search) {
        return search == null
                || search.isEmpty()
                || dto.getName().toLowerCase().contains(search.toLowerCase())
                || dto.getEan().equals(search);
    }

    private Boolean filterPrice(DenormalizedProductDto dto, Filterable.Price price) {
        if (price == null || (price.min() == null && price.max() == null)) {
            return true;
        }

        var bestPrice = dto.getProviders().values().stream()
                .min(Comparator.comparingDouble(value -> value))
                .orElse(null);

        if (bestPrice == null) {
            return false;
        }

        return (price.min() == null || bestPrice > price.min()) && (price.max() == null || bestPrice < price.max());
    }

    private Boolean filterCharacteristics(DenormalizedProductDto dto, List<List<Long>> blocks) {
        if (blocks.isEmpty()) {
            return true;
        }
        return blocks.stream().allMatch(list -> list.stream()
                .anyMatch(item -> dto.getCharacteristicIds().contains(item)));
    }
}
