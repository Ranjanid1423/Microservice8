package com.example.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orderservice.dto.ErrorResponse;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================================================
    // 1. GET ORDER + USER
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id) {

        try {

            Order order = orderService.getOrderById(id);

            if (order == null) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            Long userId = order.getUserId();

            UserResponse user =
                    orderService.getUserFromUserService(userId);

            if (user == null) {

                return ResponseEntity
                        .status(503)
                        .body(
                                new ErrorResponse(
                                        503,
                                        "User Service is currently unavailable"
                                )
                        );
            }

            OrderResponse response =
                    new OrderResponse(
                            order.getOrderId(),
                            order.getUserId(),
                            user
                    );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            System.out.println(
                    "User Service error: "
                    + e.getMessage()
            );

            ErrorResponse errorResponse =
                    new ErrorResponse(
                            503,
                            "User Service is currently unavailable"
                    );

            return ResponseEntity
                    .status(503)
                    .body(errorResponse);
        }
    }

    // =========================================================
    // 2. NORMAL PAYMENT
    // =========================================================

    @GetMapping("/{orderId}/payment/{paymentId}")
    public ResponseEntity<String> getPayment(
            @PathVariable Long orderId,
            @PathVariable Long paymentId) {

        try {

            String paymentResponse =
                    orderService.getPayment(paymentId);

            return ResponseEntity.ok(
                    "Order " + orderId
                    + " -> "
                    + paymentResponse
            );

        } catch (Exception e) {

            System.out.println(
                    "Payment Service error: "
                    + e.getMessage()
            );

            return ResponseEntity
                    .status(500)
                    .body(
                            "Payment Service call failed: "
                            + e.getClass().getSimpleName()
                    );
        }
    }

    // =========================================================
    // 3. SLOW PAYMENT / TIMEOUT TEST
    // =========================================================

    @GetMapping("/{orderId}/slow-payment/{seconds}")
    public ResponseEntity<String> getSlowPayment(
            @PathVariable Long orderId,
            @PathVariable int seconds) {

        try {

            System.out.println(
                    "Calling Payment Service..."
            );

            System.out.println(
                    "Order ID: " + orderId
            );

            System.out.println(
                    "Requested delay: "
                    + seconds
                    + " seconds"
            );

            String paymentResponse =
                    orderService.getSlowPayment(seconds);

            return ResponseEntity.ok(
                    "Order " + orderId
                    + " -> "
                    + paymentResponse
            );

        } catch (Exception e) {

            System.out.println(
                    "Payment Service timed out or failed: "
                    + e.getClass().getSimpleName()
            );

            System.out.println(
                    "Message: "
                    + e.getMessage()
            );

            return ResponseEntity
                    .status(500)
                    .body(
                            "Payment Service timed out or failed: "
                            + e.getClass().getSimpleName()
                    );
        }
    }

    // =========================================================
    // 4. RETRY PAYMENT TEST
    // =========================================================

    @GetMapping("/{orderId}/retry-payment")
    public ResponseEntity<String> retryPayment(
            @PathVariable Long orderId) {

        try {

            System.out.println(
                    "Calling Payment Service retry endpoint..."
            );

            String response =
                    orderService.retryPayment();

            return ResponseEntity.ok(
                    "Order " + orderId
                    + " -> "
                    + response
            );

        } catch (Exception e) {

            System.out.println(
                    "Payment retry failed: "
                    + e.getClass().getSimpleName()
            );

            System.out.println(
                    "Message: "
                    + e.getMessage()
            );

            return ResponseEntity
                    .status(500)
                    .body(
                            "Payment retry failed: "
                            + e.getClass().getSimpleName()
                    );
        }
    }

    // =========================================================
    // 5. PAYMENT FAILURE TEST
    // =========================================================

    @GetMapping("/{orderId}/fail-payment")
    public ResponseEntity<String> failPayment(
            @PathVariable Long orderId) {

        try {

            System.out.println(
                    "Calling Payment Service failure endpoint..."
            );

            String response =
                    orderService.failPayment();

            return ResponseEntity.ok(
                    "Order " + orderId
                    + " -> "
                    + response
            );

        } catch (Exception e) {

            System.out.println(
                    "Payment Service failure: "
                    + e.getClass().getSimpleName()
            );

            return ResponseEntity
                    .status(500)
                    .body(
                            "Payment Service failed: "
                            + e.getClass().getSimpleName()
                    );
        }
    }
}