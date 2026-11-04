package com.example.hcl_assessment.controller;

import com.example.hcl_assessment.model.CustomerOrder;
import com.example.hcl_assessment.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{customerId}")
    public ResponseEntity<CustomerOrder> placeOrder(@PathVariable Long customerId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(customerId));
    }

    @GetMapping("/customer/{customerId}")
    public List<CustomerOrder> getOrderHistory(@PathVariable Long customerId) {
        return orderService.getOrderHistory(customerId);
    }

    @PutMapping("/cancel/{orderId}")
    public CustomerOrder cancelOrder(@PathVariable Long orderId) {
        return orderService.cancelOrder(orderId);
    }
}
