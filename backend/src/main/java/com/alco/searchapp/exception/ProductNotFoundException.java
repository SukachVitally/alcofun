package com.alco.searchapp.exception;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException(Long productId) {
        super(String.format("Product with id '%s' not found", productId), null);
    }
}
