package com.example.subscription.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record DiscountDto(DiscountType type, BigDecimal value) {
    public DiscountDto {
        Objects.requireNonNull(type, "discount.type is required");
        Objects.requireNonNull(value, "discount.value is required");
        if (value.signum() < 0) {
            throw new IllegalArgumentException("discount.value cannot be negative");
        }
    }
}

