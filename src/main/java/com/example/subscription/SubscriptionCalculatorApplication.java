package com.example.subscription;

import com.example.subscription.dto.BillingRequestDto;
import com.example.subscription.dto.BillingResponseDto;
import com.example.subscription.http.JsonMapper;
import com.example.subscription.http.JsonWriter;
import com.example.subscription.service.BillingSchedule;
import com.example.subscription.service.SubscriptionCalculator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class SubscriptionCalculatorApplication {
    private static final int DEFAULT_PORT = 8080;

    private final SubscriptionCalculator calculator;
    private final BillingSchedule billingSchedule;

    public SubscriptionCalculatorApplication(SubscriptionCalculator calculator, BillingSchedule billingSchedule) {
        this.calculator = Objects.requireNonNull(calculator);
        this.billingSchedule = Objects.requireNonNull(billingSchedule);
    }

    public static void main(String[] args) throws IOException {
        SubscriptionCalculatorApplication app = new SubscriptionCalculatorApplication(
                new SubscriptionCalculator(),
                new BillingSchedule(Clock.systemDefaultZone())
        );
        app.start(portFrom(args));
        awaitShutdown();
    }

    private static void awaitShutdown() {
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static int portFrom(String[] args) {
        if (args.length == 0) {
            return DEFAULT_PORT;
        }
        try {
            int port = Integer.parseInt(args[0]);
            if (port < 1 || port > 65_535) {
                throw new IllegalArgumentException("Port must be between 1 and 65535");
            }
            return port;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Port must be a number", ex);
        }
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleUi);
        server.createContext("/api/calculate", this::handleCalculate);
        server.setExecutor(null);
        server.start();
        System.out.println("Subscription calculator running at http://localhost:" + port);
    }

    private void handleCalculate(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, "application/json", "{\"error\":\"Only POST is supported\"}");
            return;
        }

        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            BillingRequestDto request = JsonMapper.toBillingRequest(requestBody);
            BillingResponseDto response = calculator.calculate(request, billingSchedule.nextBillDate());
            send(exchange, 200, "application/json", JsonWriter.billingResponse(response));
        } catch (IllegalArgumentException ex) {
            send(exchange, 400, "application/json", JsonWriter.error(ex.getMessage()));
        } catch (Exception ex) {
            send(exchange, 500, "application/json", JsonWriter.error("Unexpected error while calculating billing amount"));
        }
    }

    private void handleUi(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 405, "text/plain", "Only GET is supported");
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (!"/".equals(path) && !"/index.html".equals(path)) {
            send(exchange, 404, "text/plain", "Not found");
            return;
        }

        try (InputStream stream = getClass().getResourceAsStream("/static/index.html")) {
            if (stream == null) {
                send(exchange, 500, "text/plain", "UI file not found");
                return;
            }
            send(exchange, 200, "text/html; charset=utf-8", new String(stream.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    private void send(HttpExchange exchange, int statusCode, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
