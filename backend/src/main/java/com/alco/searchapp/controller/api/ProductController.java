package com.alco.searchapp.controller.api;

import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ListResponseDto;
import com.alco.searchapp.dto.PriceRangeDto;
import com.alco.searchapp.dto.ProductFullDto;
import com.alco.searchapp.dto.ProductPreviewDto;
import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.entity.elastic.ProductSearchEntity;
import com.alco.searchapp.exception.ProductNotFoundException;
import com.alco.searchapp.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
@RequestMapping("/api/product/")
@RequiredArgsConstructor
public class ProductController {
    private final ProductSearchService productSearchService;

    @GetMapping("/")
    public Mono<ListResponseDto<ProductPreviewDto>> getList(Pageable pageable, Filterable filter) {
        return productSearchService.getProducts(filter, pageable)
                .map(dto -> ListResponseDto.<ProductPreviewDto>builder()
                        .currentPage(dto.getCurrentPage())
                        .pageCount(dto.getPageCount())
                        .results(dto.getResults().stream()
                                .map(this::map)
                                .toList())
                        .build());
    }

    @GetMapping("/{id}")
    public Mono<ProductFullDto> getProduct(@PathVariable Long id) {
        return productSearchService.getProduct(id)
                .map(entity -> ProductFullDto.builder()
                                .id(entity.getId())
                                .name(entity.getName())
                                .ean(entity.getEan())
                                .description(entity.getDescription())
                                .categoryId(entity.getCategoryId())
                                .categoryName(entity.getCategoryName())
                                .characteristics(entity.getCharacteristics())
                                .imageUrl(entity.getImageUrls().stream().findFirst().orElse(null))
                                .providers(entity.getProviders())
                                .rating(new RatingDto(entity.getCompositeRating(), entity.getVotes()))
                                .hot(entity.getHot())
                                .percent(entity.getPercent() != null ? entity.getPercent().intValue() : null)
                                .build());
    }

    @GetMapping("/price-range")
    public Mono<PriceRangeDto> getPriceRange() {
        return productSearchService.getMinPrice()
                .flatMap(minPrice -> productSearchService.getMaxPrice()
                        .map(maxPrice -> PriceRangeDto.builder()
                                .min(minPrice)
                                .max(maxPrice)
                                .build()));

    }

    private ProductPreviewDto map(ProductSearchEntity entity) {
        return ProductPreviewDto.builder()
                .id(entity.getId())
                .ean(entity.getEan())
                .name(entity.getName())
                .price(entity.getPrice())
                .imageUrl(entity.getImageUrls().stream().findFirst().orElse(null))
                .rating(new RatingDto(entity.getCompositeRating(), entity.getVotes()))
                .hot(entity.getHot())
                .percent(entity.getPercent().intValue())
                .build();
    }
}
