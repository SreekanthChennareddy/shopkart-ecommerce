package com.shopkart.product.repository;

import com.shopkart.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    // Query by SEO-friendly slug!
    Optional<Category> findBySlug(String slug);

    // Fetch only top-level root categories (e.g., Mobiles, Fashion, Electronics)
    List<Category> findByParentIsNull();

    boolean existsByName(String name);

    boolean existsBySlug(String slug);
}