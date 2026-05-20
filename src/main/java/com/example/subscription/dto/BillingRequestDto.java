package com.example.subscription.dto;

import java.util.List;
import java.util.Objects;

public record BillingRequestDto(
        BillingFrequency billingFrequency,
        String currency,
        List<LineItemDto> lineItems
) {
    public BillingRequestDto {
        Objects.requireNonNull(billingFrequency, "billingFrequency is required");
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency is required");
        }
        if (lineItems == null || lineItems.isEmpty()) {
            throw new IllegalArgumentException("line_items must contain at least one item");
        }
        lineItems = List.copyOf(lineItems);
    }
}

