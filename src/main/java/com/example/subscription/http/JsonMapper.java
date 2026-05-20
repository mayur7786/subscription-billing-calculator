package com.example.subscription.http;

import com.example.subscription.dto.BillingFrequency;
import com.example.subscription.dto.BillingRequestDto;
import com.example.subscription.dto.DiscountDto;
import com.example.subscription.dto.DiscountType;
import com.example.subscription.dto.LineItemDto;
import com.example.subscription.dto.PriceModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class JsonMapper {
    private JsonMapper() {
    }

    public static BillingRequestDto toBillingRequest(String json) {
        Object parsed = new SimpleJsonParser(json).parse();
        if (!(parsed instanceof Map<?, ?> root)) {
            throw new IllegalArgumentException("Request body must be a JSON object");
        }

        Object itemsValue = root.get("line_items");
        if (!(itemsValue instanceof List<?> items)) {
            throw new IllegalArgumentException("line_items must be an array");
        }

        List<LineItemDto> lineItems = new ArrayList<>();
        for (Object itemValue : items) {
            if (!(itemValue instanceof Map<?, ?> item)) {
                throw new IllegalArgumentException("Each line item must be an object");
            }
            lineItems.add(toLineItem(item));
        }

        return new BillingRequestDto(
                BillingFrequency.from(asString(root.get("billing_frequency"), "billing_frequency")),
                asString(root.get("currency"), "currency"),
                lineItems
        );
    }

    private static LineItemDto toLineItem(Map<?, ?> item) {
        DiscountDto discount = null;
        Object discountValue = item.get("discount");
        if (discountValue instanceof Map<?, ?> discountMap) {
            discount = new DiscountDto(
                    DiscountType.from(asString(discountMap.get("type"), "discount.type")),
                    asDecimal(discountMap.get("value"), "discount.value")
            );
        }

        return new LineItemDto(
                asString(item.get("product"), "product"),
                PriceModel.from(asString(item.get("price_model"), "price_model")),
                asDecimal(item.get("unit_price"), "unit_price"),
                asInteger(item.get("quantity"), "quantity"),
                discount
        );
    }

    private static String asString(Object value, String field) {
        if (value instanceof String stringValue) {
            return stringValue;
        }
        throw new IllegalArgumentException(field + " must be a string");
    }

    private static BigDecimal asDecimal(Object value, String field) {
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        throw new IllegalArgumentException(field + " must be a number");
    }

    private static int asInteger(Object value, String field) {
        if (value instanceof BigDecimal decimal && decimal.stripTrailingZeros().scale() <= 0) {
            return decimal.intValueExact();
        }
        throw new IllegalArgumentException(field + " must be an integer");
    }
}

