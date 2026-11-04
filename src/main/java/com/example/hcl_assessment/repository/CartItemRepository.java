package com.example.hcl_assessment.repository;

import com.example.hcl_assessment.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteByProduct_ProductId(Long productId);
}
