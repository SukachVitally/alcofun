package com.alco.searchapp.controller.api;

import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ListResponseDto;
import com.alco.searchapp.dto.ProductDto;
import com.alco.searchapp.dto.ProductInfoDto;
import com.alco.searchapp.service.ProductService;
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
    private final ProductService productService;

    @GetMapping("/")
    public Mono<ListResponseDto<ProductInfoDto>> getList(Pageable pageable, Filterable filter) {
        return productService.getProducts(filter, pageable)
                .collectList()
                .flatMap(list -> productService.getProductCount(filter)
                        .map(count -> ListResponseDto.<ProductInfoDto>builder()
                                .results(list)
                                .currentPage(pageable.getPageNumber())
                                .pageCount((int) Math.ceil((float) count / pageable.getPageSize()))
                                .build()));
    }

    @GetMapping("/{id}")
    public Mono<ProductDto> getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }
}
