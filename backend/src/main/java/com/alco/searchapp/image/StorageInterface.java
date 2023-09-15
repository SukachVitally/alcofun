package com.alco.searchapp.image;

import reactor.core.publisher.Mono;

public interface StorageInterface {
    Mono<Boolean> save(String bucket, String filename, byte[] content);
}
