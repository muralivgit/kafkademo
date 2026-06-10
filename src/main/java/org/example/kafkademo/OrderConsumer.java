package org.example.kafkademo;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    @RetryableTopic(
            attempts = "3", // Attempt 1 initial + 2 retries
            backoff = @Backoff(delay = 2000), // Wait 2 seconds between retries
            dltTopicSuffix = "-dlt" // Append this to create the Dead Letter Topic
    )
    @KafkaListener(topics = "springbboot-orders", groupId = "enterprise-group-v1")
    public void consumeOrder(Order order) {
        System.out.printf("🎉 [Worker Thread] Processing Order: %s (%s) for $%.2f%n",
                order.getOrderId(), order.getItemName(), order.getPrice());

        // SIMULATION: If someone sends a negative price, simulate a database crash or code bug
        if (order.getPrice() < 0) {
            System.err.printf("⚠️ [Worker Thread] Invalid price detected for Order %s! Forcing retry mechanism...%n", order.getOrderId());
            throw new IllegalArgumentException("Price cannot be negative!");
        }
    }

    // This method captures messages that failed all retry attempts
    @DltHandler
    public void handleDeadLetterMessage(Order order) {
        System.err.printf("🚨 [DLT Handler] CRITICAL! Order %s failed all retries. Moved to Dead Letter Topic for manual review.%n",
                order.getOrderId());
    }
}
