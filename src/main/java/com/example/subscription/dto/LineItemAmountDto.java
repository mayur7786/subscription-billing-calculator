package com.example.subscription.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record LineItemAmountDto(
        String product,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal amount
) {
    public LineItemAmountDto {
        if (product == null || product.isBlank()) {
            throw new IllegalArgumentException("product is required");
        }
        Objects.requireNonNull(subtotal, "subtotal is required");
        Objects.requireNonNull(discountAmount, "discountAmount is required");
        Objects.requireNonNull(amount, "amount is required");
    }
}

