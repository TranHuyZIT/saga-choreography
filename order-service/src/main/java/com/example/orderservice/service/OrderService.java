package com.example.orderservice.service;

import com.example.commondtos.dto.OrderRequestDTO;
import com.example.commondtos.enums.OrderStatus;
import com.example.orderservice.entity.PurchaseOrder;
import com.example.orderservice.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final OrderStatusPublisher orderStatusPublisher;

    @Transactional
    public PurchaseOrder createOrder(OrderRequestDTO orderRequestDto) {
        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .productId(orderRequestDto.getProductId())
                .userId(orderRequestDto.getUserId())
                .price(orderRequestDto.getAmount())
                .orderStatus(OrderStatus.ORDER_CREATED)
                .build();
        orderRequestDto.setOrderId(purchaseOrder.getId());
        // Send Kafka event to payment service
        orderStatusPublisher.publishOrderEvent(orderRequestDto, OrderStatus.ORDER_CREATED);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }
}
