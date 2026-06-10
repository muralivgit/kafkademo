package org.example.kafkademo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducerService {

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    private static final String TOPIC = "springbboot-orders";

    public void sendOrderEvent(Order order) {
        System.out.printf("🚀 [Main Thread] Passing Order %s to KafkaTemplate buffer...%n", order.getOrderId());

        // Send returns a CompletableFuture in Spring Boot 3
        kafkaTemplate.send(TOPIC, order.getOrderId(), order)
                .whenComplete((result, exception) -> {
                    if (exception == null) {
                        System.out.printf("📥 [Async Callback] SUCCESS! Order %s sent to Partition: %d at Offset: %d%n",
                                order.getOrderId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        System.err.printf("❌ [Async Callback] FAILURE sending Order %s: %s%n",
                                order.getOrderId(), exception.getMessage());
                    }
                });
    }
}
