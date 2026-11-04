package com.example.hcl_assessment.repository;

import com.example.hcl_assessment.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByCustomer_CustomerIdOrderByOrderDateDesc(Long customerId);
    boolean existsByCustomer_CustomerId(Long customerId);
}
