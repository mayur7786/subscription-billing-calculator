package com.example.subscription.service;

import com.example.subscription.dto.BillingResponseDto;
import com.example.subscription.dto.DiscountDto;
import com.example.subscription.dto.DiscountType;
import com.example.subscription.dto.LineItemAmountDto;
import com.example.subscription.dto.LineItemDto;
import com.example.subscription.dto.BillingRequestDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionCalculator {
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    public BillingResponseDto calculate(BillingRequestDto request, LocalDate billDate) {
        List<LineItemAmountDto> calculatedItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (LineItemDto item : request.lineItems()) {
            LineItemAmountDto calculatedItem = calculateLineItem(item);
            calculatedItems.add(calculatedItem);
            subtotal = subtotal.add(calculatedItem.subtotal());
            totalDiscount = totalDiscount.add(calculatedItem.discountAmount());
            totalAmount = totalAmount.add(calculatedItem.amount());
        }

        return new BillingResponseDto(
                request.billingFrequency(),
                request.currency(),
                billDate,
                calculatedItems,
                money(subtotal),
                money(totalDiscount),
                money(totalAmount)
        );
    }

    public LineItemAmountDto calculateLineItem(LineItemDto item) {
        BigDecimal subtotal = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));
        BigDecimal discountAmount = calculateDiscount(subtotal, item.discount());
        BigDecimal amount = subtotal.subtract(discountAmount);
        if (amount.signum() < 0) {
            amount = BigDecimal.ZERO;
        }

        return new LineItemAmountDto(
                item.product(),
                money(subtotal),
                money(discountAmount.min(subtotal)),
                money(amount)
        );
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, DiscountDto discount) {
        if (discount == null) {
            return BigDecimal.ZERO;
        }
        if (discount.type() == DiscountType.PERCENTAGE) {
            return subtotal.multiply(discount.value()).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
        }
        //TODO add logic for FIXED discount type when supported
        return discount.value();
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}

