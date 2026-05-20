package com.example.subscription;

import com.example.subscription.dto.BillingFrequency;
import com.example.subscription.dto.BillingRequestDto;
import com.example.subscription.dto.BillingResponseDto;
import com.example.subscription.dto.DiscountDto;
import com.example.subscription.dto.DiscountType;
import com.example.subscription.dto.LineItemDto;
import com.example.subscription.dto.PriceModel;
import com.example.subscription.http.JsonMapper;
import com.example.subscription.service.BillingSchedule;
import com.example.subscription.service.SubscriptionCalculator;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public class SubscriptionCalculatorTest {
    public static void main(String[] args) {
        percentageDiscountIsDeductedFromSubtotal();
        fixedDiscountIsDeductedFromSubtotal();
        discountCannotMakeAmountNegative();
        parserBuildsBillingRequestDto();
        nextBillDateIsFirstOfNextMonthWhenTodayIsNotFirst();
        nextBillDateIsTodayWhenTodayIsFirst();
        System.out.println("All subscription calculator tests passed");
    }

    private static void percentageDiscountIsDeductedFromSubtotal() {
        BillingResponseDto response = new SubscriptionCalculator().calculate(
                request(new LineItemDto(
                        "prod1",
                        PriceModel.FLAT,
                        BigDecimal.valueOf(500),
                        3,
                        new DiscountDto(DiscountType.PERCENTAGE, BigDecimal.valueOf(10))
                )),
                LocalDate.of(2026, 6, 1)
        );

        assertMoney("1500.00", response.subtotal());
        assertMoney("150.00", response.totalDiscount());
        assertMoney("1350.00", response.totalAmount());
    }

    private static void fixedDiscountIsDeductedFromSubtotal() {
        BillingResponseDto response = new SubscriptionCalculator().calculate(
                request(new LineItemDto(
                        "prod2",
                        PriceModel.FLAT,
                        BigDecimal.valueOf(120),
                        2,
                        new DiscountDto(DiscountType.FIXED, BigDecimal.valueOf(40))
                )),
                LocalDate.of(2026, 6, 1)
        );

        assertMoney("240.00", response.subtotal());
        assertMoney("40.00", response.totalDiscount());
        assertMoney("200.00", response.totalAmount());
    }

    private static void discountCannotMakeAmountNegative() {
        BillingResponseDto response = new SubscriptionCalculator().calculate(
                request(new LineItemDto(
                        "prod3",
                        PriceModel.FLAT,
                        BigDecimal.valueOf(25),
                        1,
                        new DiscountDto(DiscountType.FIXED, BigDecimal.valueOf(100))
                )),
                LocalDate.of(2026, 6, 1)
        );

        assertMoney("25.00", response.totalDiscount());
        assertMoney("0.00", response.totalAmount());
    }

    private static void parserBuildsBillingRequestDto() {
        BillingRequestDto request = JsonMapper.toBillingRequest("""
                {
                  "billing_frequency": "monthly",
                  "currency": "USD",
                  "line_items": [
                    {
                      "product": "prod1",
                      "price_model": "flat",
                      "unit_price": 500,
                      "quantity": 3,
                      "discount": { "type": "percentage", "value": 10 }
                    }
                  ]
                }
                """);

        assertEquals(BillingFrequency.MONTHLY, request.billingFrequency());
        assertEquals("USD", request.currency());
        assertEquals(1, request.lineItems().size());
        assertEquals("prod1", request.lineItems().getFirst().product());
    }

    private static void nextBillDateIsFirstOfNextMonthWhenTodayIsNotFirst() {
        BillingSchedule schedule = new BillingSchedule(Clock.fixed(
                Instant.parse("2026-05-19T12:00:00Z"),
                ZoneOffset.UTC
        ));

        assertEquals(LocalDate.of(2026, 6, 1), schedule.nextBillDate());
    }

    private static void nextBillDateIsTodayWhenTodayIsFirst() {
        BillingSchedule schedule = new BillingSchedule(Clock.fixed(
                Instant.parse("2026-05-01T12:00:00Z"),
                ZoneOffset.UTC
        ));

        assertEquals(LocalDate.of(2026, 5, 1), schedule.nextBillDate());
    }

    private static BillingRequestDto request(LineItemDto item) {
        return new BillingRequestDto(BillingFrequency.MONTHLY, "USD", List.of(item));
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(new BigDecimal(expected), actual);
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}

