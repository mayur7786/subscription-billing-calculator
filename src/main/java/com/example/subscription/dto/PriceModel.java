package com.example.subscription.dto;

public enum PriceModel {
    FLAT;

    public static PriceModel from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("price_model is required");
        }
        if ("flat".equalsIgnoreCase(value)) {
            return FLAT;
        }
        throw new IllegalArgumentException("Only flat price_model is supported");
    }
}

