package com.example.orderservice.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.retry.annotation.Retry;

@Component
public class PaymentClient {

    private final RestTemplate restTemplate;

    private static final String PAYMENT_SERVICE_URL =
            "http://localhost:8082/api/payments";

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // =========================================================
    // NORMAL PAYMENT
    // =========================================================

    public String getPayment(Long paymentId) {

        String url =
                PAYMENT_SERVICE_URL
                + "/"
                + paymentId;

        System.out.println(
                "Calling Payment Service: "
                + url
        );

        return restTemplate.getForObject(
                url,
                String.class
        );
    }

    // =========================================================
    // SLOW PAYMENT
    // =========================================================

    public String getSlowPayment(int seconds) {

        String url =
                PAYMENT_SERVICE_URL
                + "/slow/"
                + seconds;

        System.out.println(
                "Calling Payment Service: "
                + url
        );

        return restTemplate.getForObject(
                url,
                String.class
        );
    }

    // =========================================================
    // RETRY PAYMENT
    // =========================================================
    //
    // Resilience4j handles the retry.
    //
    // Attempt 1 -> Payment Service returns 500
    // Attempt 2 -> Payment Service returns 200
    //
    // Configuration is in application.properties:
    //
    // resilience4j.retry.instances.paymentRetry.max-attempts=2
    //
    // =========================================================

    @Retry(name = "paymentRetry")
    public String retryPayment() {

        String url =
                PAYMENT_SERVICE_URL
                + "/retry-test";

        System.out.println(
                "Calling Payment Retry: "
                + url
        );

        return restTemplate.getForObject(
                url,
                String.class
        );
    }

    // =========================================================
    // FAIL PAYMENT
    // =========================================================

    public String failPayment() {

        String url =
                PAYMENT_SERVICE_URL
                + "/fail";

        System.out.println(
                "Calling Payment Failure: "
                + url
        );

        return restTemplate.getForObject(
                url,
                String.class
        );
    }

    // =========================================================
    // RESET RETRY TEST
    // =========================================================

    public String resetRetryTest() {

        String url =
                PAYMENT_SERVICE_URL
                + "/retry-test/reset";

        System.out.println(
                "Resetting Payment retry counter: "
                + url
        );

        return restTemplate.getForObject(
                url,
                String.class
        );
    }

    // =========================================================
    // PAYMENT HEALTH
    // =========================================================

    public String healthCheck() {

        String url =
                PAYMENT_SERVICE_URL
                + "/health";

        System.out.println(
                "Checking Payment Service: "
                + url
        );

        return restTemplate.getForObject(
                url,
                String.class
        );
    }
}