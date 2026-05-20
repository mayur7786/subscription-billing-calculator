package com.example.subscription.dto;

public enum DiscountType {
    PERCENTAGE,
    FIXED;

    public static DiscountType from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("discount.type is required");
        }
        return switch (value.toLowerCase()) {
            case "percentage" -> PERCENTAGE;
            case "fixed" -> FIXED;
            default -> throw new IllegalArgumentException("discount.type must be percentage or fixed");
        };
    }
}

