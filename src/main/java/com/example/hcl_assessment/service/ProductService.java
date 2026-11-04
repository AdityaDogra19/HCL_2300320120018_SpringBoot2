package com.example.hcl_assessment.service;

import com.example.hcl_assessment.exception.BadRequestException;
import com.example.hcl_assessment.exception.ResourceNotFoundException;
import com.example.hcl_assessment.model.Product;
import com.example.hcl_assessment.repository.CartItemRepository;
import com.example.hcl_assessment.repository.OrderItemRepository;
import com.example.hcl_assessment.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public Product addProduct(Product product) {
        product.setProductId(null);
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product updateProduct(Long id, Product updated) {
        Product existing = getProductById(id);
        existing.setProductName(updated.getProductName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setQuantity(updated.getQuantity());
        existing.setCategory(updated.getCategory());
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        if (orderItemRepository.existsByProduct_ProductId(id)) {
            throw new BadRequestException("Product is part of existing orders and cannot be deleted");
        }
        cartItemRepository.deleteByProduct_ProductId(id); 
        productRepository.delete(product);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category.trim());
    }

    public List<Product> searchByName(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name.trim());
    }
}
