package com.shopkart.product.service;

import com.shopkart.common.exception.ResourceNotFoundException;
import com.shopkart.product.dto.CategoryResponse;
import com.shopkart.product.entity.Category;
import com.shopkart.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'root'")
    public List<CategoryResponse> getRootCategories() {
        log.info("Fetching root categories from DATABASE (Cache Miss)");
        List<Category> categories = categoryRepository.findByParentIsNull();
        return categories.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
        return mapToResponse(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .iconUrl(category.getIconUrl())
                .subCategories(category.getSubCategories() != null
                        ? category.getSubCategories().stream().map(this::mapToResponse).collect(Collectors.toList())
                        : List.of())
                .build();
    }
}