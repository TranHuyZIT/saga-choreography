package com.example.orderservice.service;

import com.example.commondtos.dto.OrderRequestDTO;
import com.example.commondtos.enums.OrderStatus;
import com.example.commondtos.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OrderStatusPublisher {
    @Autowired
    private Sinks.Many<OrderEvent> orderSinks = Sinks.many().multicast().onBackpressureBuffer();

    public void publishOrderEvent(OrderRequestDTO orderRequestDto, OrderStatus orderStatus){
        System.out.println(orderSinks);
        OrderEvent orderEvent=new OrderEvent(orderRequestDto,orderStatus);
        orderSinks.tryEmitNext(orderEvent);
    }
}
