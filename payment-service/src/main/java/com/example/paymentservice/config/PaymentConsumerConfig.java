package com.example.paymentservice.config;

import com.example.commondtos.enums.OrderStatus;
import com.example.commondtos.event.OrderEvent;
import com.example.commondtos.event.PaymentEvent;
import com.example.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class PaymentConsumerConfig {

    @Autowired
    private PaymentService paymentService;

    @Bean
    public Function<Flux<OrderEvent>, Flux<PaymentEvent>> paymentProcessor() {
        System.out.println("PaymentService.processPayment");
        return orderEventFlux -> orderEventFlux.flatMap(this::processPayment);
    }

    // Listen to OrderEvent and process PaymentEvent
    private Mono<PaymentEvent> processPayment(OrderEvent orderEvent) {
        System.out.println("PaymentService.processPayment: " + orderEvent);
        if(OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())){
            return  Mono.fromSupplier(()->this.paymentService.newOrderEvent(orderEvent));
        }else{
            return Mono.fromRunnable(()->this.paymentService.cancelOrderEvent(orderEvent));
        }
    }
}