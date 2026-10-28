package com.shopkart.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_variants", indexes = {
        @Index(name = "idx_variant_sku", columnList = "sku")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, unique = true, length = 100)
    private String sku; // e.g. "IPH15-BLK-128"

    private String color;
    private String size;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // MRP

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice; // Discounted Price

    @Column(nullable = false)
    private Integer stockQuantity;

    // Multiple images per variant!
    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @Version
    private Long version;
}