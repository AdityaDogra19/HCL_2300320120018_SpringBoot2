package com.example.hcl_assessment.controller;

import com.example.hcl_assessment.model.Cart;
import com.example.hcl_assessment.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add/{customerId}/{productId}")
    public Cart addToCart(@PathVariable Long customerId,
                          @PathVariable Long productId,
                          @RequestParam(defaultValue = "1") int quantity) {
        return cartService.addToCart(customerId, productId, quantity);
    }

    @GetMapping("/{customerId}")
    public Cart viewCart(@PathVariable Long customerId) {
        return cartService.viewCart(customerId);
    }

    @PutMapping("/update/{cartItemId}")
    public Cart updateQuantity(@PathVariable Long cartItemId, @RequestParam int quantity) {
        return cartService.updateQuantity(cartItemId, quantity);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public Cart removeItem(@PathVariable Long cartItemId) {
        return cartService.removeItem(cartItemId);
    }

    @DeleteMapping("/clear/{customerId}")
    public Map<String, String> clearCart(@PathVariable Long customerId) {
        cartService.clearCart(customerId);
        return Map.of("message", "Cart cleared successfully");
    }
}
