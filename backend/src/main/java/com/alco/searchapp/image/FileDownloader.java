package com.alco.searchapp.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FileDownloader {
    private final WebClient client;

    public Mono<byte[]> download(String url) {
        return client.get()
                .uri(url)
                .exchangeToMono(response -> (response.statusCode().is2xxSuccessful())
                            ? response.bodyToMono(byte[].class)
                            : response.createException().flatMap(Mono::error));
    }
}
