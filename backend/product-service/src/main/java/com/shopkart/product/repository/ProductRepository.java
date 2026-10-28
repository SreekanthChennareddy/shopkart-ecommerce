package com.shopkart.product.repository;

import com.shopkart.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Paginated products by category
    Page<Product> findByCategoryIdAndActiveTrue(UUID categoryId, Pageable pageable);

    // Search by title or brand
    Page<Product> findByTitleContainingIgnoreCaseOrBrandContainingIgnoreCaseAndActiveTrue(
            String title, String brand, Pageable pageable);

    // Seller's product list
    Page<Product> findBySellerId(UUID sellerId, Pageable pageable);
}