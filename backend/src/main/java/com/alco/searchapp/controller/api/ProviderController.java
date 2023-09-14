package com.alco.searchapp.controller.api;

import com.alco.searchapp.dto.CategoryDto;
import com.alco.searchapp.dto.ProviderDto;
import com.alco.searchapp.service.CategoryService;
import com.alco.searchapp.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@CrossOrigin
@RestController
@RequestMapping("/api/provider/")
@RequiredArgsConstructor
public class ProviderController {
    private final ProviderService service;

    @GetMapping("/")
    public Flux<ProviderDto> getList() {
        return service.getProviders();
    }
}
