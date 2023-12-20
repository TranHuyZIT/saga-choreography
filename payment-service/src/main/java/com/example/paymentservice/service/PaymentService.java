package com.example.paymentservice.service;

import com.example.commondtos.dto.OrderRequestDTO;
import com.example.commondtos.dto.PaymentRequestDTO;
import com.example.commondtos.enums.PaymentStatus;
import com.example.commondtos.event.OrderEvent;
import com.example.commondtos.event.PaymentEvent;
import com.example.paymentservice.entity.UserBalance;
import com.example.paymentservice.repository.UserBalanceRepository;
import com.example.paymentservice.repository.UserTransactionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserBalanceRepository userBalanceRepository;
    private final UserTransactionRepository userTransactionRepository;

    @PostConstruct
    public void initUserBalanceInDB() {
        userBalanceRepository.saveAll(Stream.of(new UserBalance(101, 5000),
                new UserBalance(102, 3000),
                new UserBalance(103, 4200),
                new UserBalance(104, 20000),
                new UserBalance(105, 999)).collect(Collectors.toList()));
    }

    // Saga Transaction
    @Transactional
    public PaymentEvent newOrderEvent(OrderEvent orderEvent) {
        OrderRequestDTO orderRequestDTO = orderEvent.getOrderRequestDTO();
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(
                orderRequestDTO.getOrderId(),
                orderRequestDTO.getUserId(),
                orderRequestDTO.getAmount()
        );
        return userBalanceRepository.findById(paymentRequestDTO.getUserId())
                .filter(userBalance -> userBalance.getPrice() >= paymentRequestDTO.getAmount())
                .map(userBalance -> {
                    userBalance.setPrice(userBalance.getPrice() - paymentRequestDTO.getAmount());
                    userBalanceRepository.save(userBalance);
                    return new PaymentEvent(paymentRequestDTO, PaymentStatus.PAYMENT_COMPLETED);
                })
                .orElse(new PaymentEvent(paymentRequestDTO, PaymentStatus.PAYMENT_FAILED));
    }

    // Compensating Transaction
    @Transactional
    public void cancelOrderEvent(OrderEvent orderEvent) {
        OrderRequestDTO orderRequestDTO = orderEvent.getOrderRequestDTO();
        PaymentRequestDTO paymentRequestDTO = new PaymentRequestDTO(
                orderRequestDTO.getOrderId(),
                orderRequestDTO.getUserId(),
                orderRequestDTO.getAmount()
        );
        userBalanceRepository.findById(paymentRequestDTO.getUserId())
                .ifPresent(userBalance -> {
                    userBalance.setPrice(userBalance.getPrice() + paymentRequestDTO.getAmount());
                    userBalanceRepository.save(userBalance);
                });
    }
}
