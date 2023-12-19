package com.example.commondtos.event;

import com.example.commondtos.dto.OrderRequestDTO;
import com.example.commondtos.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent implements Event {
    private UUID eventId = UUID.randomUUID();
    private Date date = new Date();
    private OrderRequestDTO orderRequestDTO;
    private OrderStatus orderStatus;
}
