package com.example.subscription.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record LineItemDto(
        String product,
        PriceModel priceModel,
        BigDecimal unitPrice,
        int quantity,
        DiscountDto discount
) {
    public LineItemDto {
        if (product == null || product.isBlank()) {
            throw new IllegalArgumentException("product is required");
        }
        Objects.requireNonNull(priceModel, "price_model is required");
        Objects.requireNonNull(unitPrice, "unit_price is required");
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("unit_price cannot be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }
}

