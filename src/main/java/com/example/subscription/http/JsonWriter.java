package com.example.subscription.http;

import com.example.subscription.dto.BillingResponseDto;
import com.example.subscription.dto.LineItemAmountDto;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public final class JsonWriter {
    private JsonWriter() {
    }

    public static String billingResponse(BillingResponseDto response) {
        String items = response.lineItems().stream()
                .map(JsonWriter::lineItem)
                .collect(Collectors.joining(","));

        return "{"
                + "\"billing_frequency\":\"monthly\","
                + "\"currency\":\"" + escape(response.currency()) + "\","
                + "\"bill_date\":\"" + response.billDate() + "\","
                + "\"line_items\":[" + items + "],"
                + "\"subtotal\":" + number(response.subtotal()) + ","
                + "\"total_discount\":" + number(response.totalDiscount()) + ","
                + "\"total_amount\":" + number(response.totalAmount())
                + "}";
    }

    public static String error(String message) {
        return "{\"error\":\"" + escape(message) + "\"}";
    }

    private static String lineItem(LineItemAmountDto item) {
        return "{"
                + "\"product\":\"" + escape(item.product()) + "\","
                + "\"subtotal\":" + number(item.subtotal()) + ","
                + "\"discount_amount\":" + number(item.discountAmount()) + ","
                + "\"amount\":" + number(item.amount())
                + "}";
    }

    private static String number(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

