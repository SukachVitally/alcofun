package com.alco.searchapp.provider;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.provider.dto.Product;
import reactor.core.publisher.Flux;

public interface IProvider {
    Flux<Product> getList();

    ProviderTypeEnum getType();
}
