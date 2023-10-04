package com.alco.searchapp.controller.api;

import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ListResponseDto;
import com.alco.searchapp.dto.ProductFullDto;
import com.alco.searchapp.dto.ProductPreviewDto;
import com.alco.searchapp.service.ProductSearchService;
import com.alco.searchapp.service.ProductService;
import com.alco.searchapp.service.RatingService;
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

    private final RatingService ratingService;

    private final ProductService productService;

    @GetMapping("/")
    public Mono<ListResponseDto<ProductPreviewDto>> getList(Pageable pageable, Filterable filter) {
        return productSearchService.getProducts(filter, pageable);
    }

    @GetMapping("/{id}")
    public Mono<ProductFullDto> getProduct(@PathVariable Long id) {
        return productService.getProduct(id)
                .flatMap(productDto -> ratingService.getCompositeRating(productDto.getId())
                        .map(rating -> ProductFullDto.builder()
                                .id(productDto.getId())
                                .name(productDto.getName())
                                .ean(productDto.getEan())
                                .description(productDto.getDescription())
                                .category(productDto.getCategory())
                                .characteristics(productDto.getCharacteristics())
                                .imageUrls(productDto.getImageUrls())
                                .providers(productDto.getProviders())
                                .rating(rating)
                                .build()
                        ));
    }

    // todo: Temporary. Need to check admin access to it.
    @GetMapping(path = "/refresh")
    public Mono<Long> refreshElasticCache() {
        return productSearchService.refresh();
    }
}
