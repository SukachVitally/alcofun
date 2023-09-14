package com.alco.searchapp.controller.api;

import com.alco.searchapp.dto.CategoryDto;
import com.alco.searchapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@CrossOrigin
@RestController
@RequestMapping("/api/category/")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @GetMapping("/")
    public Flux<CategoryDto> getList() {
        return service.getCategories();
    }
}
