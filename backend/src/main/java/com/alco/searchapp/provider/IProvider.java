package com.alco.searchapp.provider;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.provider.dto.ProductInfo;
import com.alco.searchapp.provider.dto.ProductPageDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProvider {
    Flux<ProductPageDto> getProductPages();

    Mono<ProductInfo> parsePageInfo(String url);

    ProviderTypeEnum getType();
}
