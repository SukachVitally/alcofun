package com.alco.searchapp.controller.api;

import com.alco.searchapp.dto.PriceHistoryItemDto;
import com.alco.searchapp.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@CrossOrigin
@RestController
@RequestMapping("/api/price/")
@RequiredArgsConstructor
public class PriceController {
    private final ProviderService service;

    @GetMapping("/history/{id}")
    public Flux<PriceHistoryItemDto> getList(@PathVariable Long id) {
        return service.getPriceHistory(id);
    }
}
