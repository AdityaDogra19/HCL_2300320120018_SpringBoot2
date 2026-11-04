package com.example.hcl_assessment.service;

import com.example.hcl_assessment.exception.BadRequestException;
import com.example.hcl_assessment.exception.ResourceNotFoundException;
import com.example.hcl_assessment.model.Cart;
import com.example.hcl_assessment.model.CartItem;
import com.example.hcl_assessment.model.Customer;
import com.example.hcl_assessment.model.Product;
import com.example.hcl_assessment.repository.CartItemRepository;
import com.example.hcl_assessment.repository.CartRepository;
import com.example.hcl_assessment.repository.CustomerRepository;
import com.example.hcl_assessment.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart addToCart(Long customerId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }
        Customer customer = findCustomer(customerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Cart cart = getOrCreateCart(customer);

        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        int newQuantity = (item == null ? 0 : item.getQuantity()) + quantity;
        if (newQuantity > product.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock for '"
                    + product.getProductName() + "' (available: " + product.getQuantity() + ")");
        }

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            cart.getCartItems().add(item);
        }
        item.setQuantity(newQuantity);
        item.setPrice(product.getPrice()); 

        return cartRepository.saveAndFlush(cart);
    }

    public Cart viewCart(Long customerId) {
        Customer customer = findCustomer(customerId);
        return getOrCreateCart(customer);
    }

    public Cart updateQuantity(Long cartItemId, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero (use the remove endpoint to delete an item)");
        }
        CartItem item = findCartItem(cartItemId);
        Product product = item.getProduct();
        if (quantity > product.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock for '"
                    + product.getProductName() + "' (available: " + product.getQuantity() + ")");
        }
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        cartItemRepository.saveAndFlush(item);
        return item.getCart();
    }

    public Cart removeItem(Long cartItemId) {
        CartItem item = findCartItem(cartItemId);
        Cart cart = item.getCart();
        cart.getCartItems().remove(item); 
        return cartRepository.saveAndFlush(cart);
    }

    public void clearCart(Long customerId) {
        findCustomer(customerId);
        Cart cart = cartRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer id: " + customerId));
        cart.getCartItems().clear();
        cartRepository.saveAndFlush(cart);
    }

    private Customer findCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    private CartItem findCartItem(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
    }

    private Cart getOrCreateCart(Customer customer) {
        return cartRepository.findByCustomer_CustomerId(customer.getCustomerId())
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setCustomer(customer);
                    cart.setCreatedDate(LocalDate.now());
                    return cartRepository.save(cart);
                });
    }
}
