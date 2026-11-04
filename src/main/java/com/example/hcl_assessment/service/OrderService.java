package com.example.hcl_assessment.service;

import com.example.hcl_assessment.exception.BadRequestException;
import com.example.hcl_assessment.exception.ResourceNotFoundException;
import com.example.hcl_assessment.model.*;
import com.example.hcl_assessment.repository.CartRepository;
import com.example.hcl_assessment.repository.CustomerOrderRepository;
import com.example.hcl_assessment.repository.CustomerRepository;
import com.example.hcl_assessment.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private CustomerOrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    public CustomerOrder placeOrder(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));
        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            if (item.getQuantity() > product.getQuantity()) {
                throw new BadRequestException("Insufficient stock for '" + product.getProductName()
                        + "' (available: " + product.getQuantity() + ")");
            }
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(CustomerOrder.PLACED);

        double total = 0;
        for (CartItem item : cart.getCartItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setCustomerOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice()); 
            order.getOrderItems().add(orderItem);

            total += product.getPrice() * item.getQuantity();
        }
        order.setTotalAmount(Math.round(total * 100.0) / 100.0);

        CustomerOrder saved = orderRepository.save(order);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return saved;
    }

    public List<CustomerOrder> getOrderHistory(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with id: " + customerId);
        }
        return orderRepository.findByCustomer_CustomerIdOrderByOrderDateDesc(customerId);
    }

    public CustomerOrder getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    public CustomerOrder cancelOrder(Long orderId) {
        CustomerOrder order = getOrderById(orderId);
        if (!CustomerOrder.PLACED.equals(order.getOrderStatus())) {
            throw new BadRequestException("Only PLACED orders can be cancelled (current status: "
                    + order.getOrderStatus() + ")");
        }
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        order.setOrderStatus(CustomerOrder.CANCELLED);
        return orderRepository.save(order);
    }
}
