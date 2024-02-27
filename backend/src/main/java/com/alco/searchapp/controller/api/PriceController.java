package com.alco.searchapp.controller.api;

import com.alco.searchapp.dto.BestPriceHistoryItemDto;
import com.alco.searchapp.service.BestPriceService;
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
    private final BestPriceService service;

    @GetMapping("/history/{id}")
    public Flux<BestPriceHistoryItemDto> getList(@PathVariable Long id) {
        return service.getHistory(id);
    }
}
