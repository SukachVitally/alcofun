package com.alco.searchapp.exception;

public class BestPriceNotFoundException extends BaseException {
    public BestPriceNotFoundException(Long productId) {
        super(String.format("Best price not found for product id %s", productId), null);
    }
}
