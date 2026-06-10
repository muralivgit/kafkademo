package org.example.kafkademo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderProducerService producerService;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        // Trigger the asynchronous event pipeline
        producerService.sendOrderEvent(order);

        // Return a response to the user instantly before Kafka even finishes the network trip
        return ResponseEntity.ok("Order message accepted for processing!");
    }
}
