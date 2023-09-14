package com.alco.searchapp.controller.api;

import com.alco.searchapp.dto.CharacteristicDto;
import com.alco.searchapp.dto.CharacteristicTypeDto;
import com.alco.searchapp.service.CharacteristicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@CrossOrigin
@RestController
@RequestMapping("/api/characteristic/")
@RequiredArgsConstructor
public class CharacteristicController {
    private final CharacteristicService service;

    @GetMapping("/{typeId}")
    public Flux<CharacteristicDto> getList(@PathVariable Integer typeId) {
        return service.getAllForType(typeId);
    }

    @GetMapping("/types")
    public Flux<CharacteristicTypeDto> getCharacteristicTypes() {
        return service.getTypes();
    }
}
