package com.example.subscription.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record BillingResponseDto(
        BillingFrequency billingFrequency,
        String currency,
        LocalDate billDate,
        List<LineItemAmountDto> lineItems,
        BigDecimal subtotal,
        BigDecimal totalDiscount,
        BigDecimal totalAmount
) {
    public BillingResponseDto {
        Objects.requireNonNull(billingFrequency, "billingFrequency is required");
        Objects.requireNonNull(currency, "currency is required");
        Objects.requireNonNull(billDate, "billDate is required");
        lineItems = List.copyOf(Objects.requireNonNull(lineItems, "lineItems is required"));
        Objects.requireNonNull(subtotal, "subtotal is required");
        Objects.requireNonNull(totalDiscount, "totalDiscount is required");
        Objects.requireNonNull(totalAmount, "totalAmount is required");
    }
}

