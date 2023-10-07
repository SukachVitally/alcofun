package com.alco.searchapp.exception;

import com.alco.searchapp.constants.ProviderTypeEnum;

public class ProductProviderNotSupport extends ProviderException {
    public ProductProviderNotSupport(ProviderTypeEnum providerType) {
        super(String.format("Product provider %s not support.", providerType.getName()));
    }
}
