package com.example.subscription.dto;

public enum BillingFrequency {
    MONTHLY;

    public static BillingFrequency from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("billing_frequency is required");
        }
        if ("monthly".equalsIgnoreCase(value)) {
            return MONTHLY;
        }
        throw new IllegalArgumentException("Only monthly billing_frequency is supported");
    }
}

