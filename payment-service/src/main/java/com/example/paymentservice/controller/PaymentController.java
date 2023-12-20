package com.example.paymentservice.controller;

import com.example.commondtos.event.OrderEvent;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @PostMapping
    public String test(@RequestBody OrderEvent orderEvent){
        System.out.println("PaymentService.test, order event: " + orderEvent);
        return "PaymentService.test";
    }
}
