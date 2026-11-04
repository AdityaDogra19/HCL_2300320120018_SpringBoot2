package com.example.hcl_assessment.service;

import com.example.hcl_assessment.exception.BadRequestException;
import com.example.hcl_assessment.exception.ResourceNotFoundException;
import com.example.hcl_assessment.model.Customer;
import com.example.hcl_assessment.repository.CartRepository;
import com.example.hcl_assessment.repository.CustomerOrderRepository;
import com.example.hcl_assessment.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerOrderRepository orderRepository;

    public Customer register(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new BadRequestException("Email is already registered: " + customer.getEmail());
        }
        customer.setCustomerId(null); 
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    public Customer updateCustomer(Long id, Customer updated) {
        Customer existing = getCustomerById(id);

        customerRepository.findByEmail(updated.getEmail()).ifPresent(other -> {
            if (!other.getCustomerId().equals(id)) {
                throw new BadRequestException("Email is already used by another customer: " + updated.getEmail());
            }
        });

        existing.setCustomerName(updated.getCustomerName());
        existing.setEmail(updated.getEmail());
        existing.setMobile(updated.getMobile());
        existing.setCity(updated.getCity());
        if (updated.getPassword() != null && !updated.getPassword().isBlank()) {
            existing.setPassword(updated.getPassword());
        }
        return customerRepository.save(existing);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        if (orderRepository.existsByCustomer_CustomerId(id)) {
            throw new BadRequestException("Customer has order history and cannot be deleted");
        }
        cartRepository.findByCustomer_CustomerId(id).ifPresent(cartRepository::delete);
        customerRepository.delete(customer);
    }
}
