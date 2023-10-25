package com.alco.searchapp.service;

import com.alco.searchapp.entity.ProductImageEntity;
import com.alco.searchapp.image.FileDownloader;
import com.alco.searchapp.image.StorageInterface;
import com.alco.searchapp.properites.ImageServiceProperties;
import com.alco.searchapp.repository.ProductImageRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final FileDownloader downloader;

    private final StorageInterface storage;

    private final ProductImageRepository repository;

    private final ImageServiceProperties properties;

    public Mono<ProductImageEntity> addImage(String url, long productId) {
        return downloader.download(url)
                .flatMap(content -> {
                    var path = (String.format("%s-%s.jpeg", productId, UUID.randomUUID()));
                    return storage.save(properties.getBucket(), path, content)
                            .thenReturn(path);
                })
                .doOnNext(path -> log.info("File {} was saved to storage", path))
                .flatMap(path -> {
                    var entity = new ProductImageEntity();
                    entity.setProductId(productId);
                    entity.setPath(path);
                    return repository.save(entity);
                });
    }

    public Mono<List<String>> getProductImageUrls(long productId) {
        return repository.findByProductId(productId)
                .map(this::map)
                .collectList();
    }

    private String map(ProductImageEntity entity) {
        return properties.getBaseUrl() + entity.getPath();
    }
}
