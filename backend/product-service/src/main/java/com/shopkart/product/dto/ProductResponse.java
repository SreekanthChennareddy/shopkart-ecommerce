package com.shopkart.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private UUID id;
    private String title;
    private String description;
    private String brand;
    private UUID categoryId;
    private String categoryName;
    private UUID sellerId;
    private boolean active;
    private List<ProductVariantDto> variants;
    private LocalDateTime createdAt;
}