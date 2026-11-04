package com.example.hcl_assessment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "Product name is mandatory")
    @Column(nullable = false)
    private String productName;

    private String description;

    @NotNull(message = "Product price is mandatory")
    @Positive(message = "Product price must be greater than zero")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Product quantity is mandatory")
    @Min(value = 0, message = "Product quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantity;

    private String category;
}
