package com.example.subscription.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

public class BillingSchedule {
    private final Clock clock;

    public BillingSchedule(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    public LocalDate nextBillDate() {
        LocalDate today = LocalDate.now(clock);
        LocalDate firstOfThisMonth = today.withDayOfMonth(1);
        if (today.equals(firstOfThisMonth)) {
            return today;
        }
        return today.plusMonths(1).withDayOfMonth(1);
    }
}

