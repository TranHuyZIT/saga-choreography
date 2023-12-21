package com.example.orderservice.service;

import com.example.commondtos.dto.OrderRequestDTO;
import com.example.commondtos.enums.OrderStatus;
import com.example.commondtos.enums.PaymentStatus;
import com.example.commondtos.event.PaymentEvent;
import com.example.orderservice.entity.PurchaseOrder;
import com.example.orderservice.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Configuration
public class OrderConsumerHandler {
    @Autowired
    private PurchaseOrderRepository orderRepository;
    @Autowired
    private OrderStatusPublisher publisher;

    @Transactional
    public void updateOrder(Integer id, Consumer<PurchaseOrder> consumer){
        orderRepository.findById(id)
                .ifPresent(consumer.andThen(this::updateOrder));
    }

    private void updateOrder(PurchaseOrder purchaseOrder){
        boolean isPaymentComplete = PaymentStatus.PAYMENT_COMPLETED.equals(purchaseOrder.getPaymentStatus());
        OrderStatus orderStatus = isPaymentComplete ? OrderStatus.ORDER_COMPLETED : OrderStatus.ORDER_CANCELLED;
        purchaseOrder.setOrderStatus(orderStatus);
        if (!isPaymentComplete) {
            publisher.publishOrderEvent(convertEntityToDto(purchaseOrder), orderStatus);
        }
    }
    public OrderRequestDTO convertEntityToDto(PurchaseOrder purchaseOrder) {
        OrderRequestDTO orderRequestDto = new OrderRequestDTO();
        orderRequestDto.setOrderId(purchaseOrder.getId());
        orderRequestDto.setUserId(purchaseOrder.getUserId());
        orderRequestDto.setAmount(purchaseOrder.getPrice());
        orderRequestDto.setProductId(purchaseOrder.getProductId());
        return orderRequestDto;
    }
}
