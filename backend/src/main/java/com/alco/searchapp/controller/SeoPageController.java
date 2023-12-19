package com.alco.searchapp.controller;

import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ListResponseDto;
import com.alco.searchapp.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/seo")
@RequiredArgsConstructor
public class SeoPageController {
    private static final int PRODUCT_LIMIT = 100;

    private final ProductSearchService productService;

    @GetMapping("/")
    public Mono<Rendering> getMain(Model model) {
        Filterable filterable = Filterable.builder().hot(true).build();
        return renderList(model, "Alcoprice UA",
                "Alcoprice UA агрегатор алкогольних продуктів в Україні пропонує найвигідніші пропрозиції на ринку",
                filterable);
    }

    @GetMapping("/products/")
    public Mono<Rendering> getProducts(Model model) {
        Filterable filterable = Filterable.builder().build();
        return renderList(model, "Alcoprice UA список продуктів",
                "Alcoprice UA агрегатор алкогольних продуктів в Україні", filterable);
    }

    @GetMapping("/products/{id}")
    public Mono<Rendering> getProduct(Model model, @PathVariable Long id) {
        return productService.getProduct(id)
                .flatMap(product -> {
                    model.addAttribute("title", product.getName());
                    model.addAttribute("description", product.getName());
                    model.addAttribute("product", product);
                    return Mono.just(Rendering.view("product").build());
                });
    }

    private Mono<Rendering> renderList(Model model, String title, String description, Filterable filterable) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);

        Pageable pageable = PageRequest.of(1, PRODUCT_LIMIT);
        return productService.getProducts(filterable, pageable)
                .map(ListResponseDto::getResults)
                .flatMap(list -> {
                    model.addAttribute("products", list);
                    return Mono.just(Rendering.view("product-list").build());
                });
    }
}
