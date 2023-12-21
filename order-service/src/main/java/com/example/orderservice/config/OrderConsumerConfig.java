package com.example.orderservice.config;

import com.example.commondtos.event.PaymentEvent;
import com.example.orderservice.service.OrderConsumerHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class OrderConsumerConfig {
    @Autowired
    private  OrderConsumerHandler orderConsumerHandler;

    @Bean
    public Consumer<PaymentEvent> paymentEventConsumer(){
        return paymentEvent -> orderConsumerHandler.updateOrder(
                paymentEvent.getPaymentRequestDTO().getOrderId(),
                purchaseOrder -> purchaseOrder.setPaymentStatus(paymentEvent.getPaymentStatus())
        );
    }
}
