package com.example.hcl_assessment.repository;

import com.example.hcl_assessment.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByProduct_ProductId(Long productId);
}
