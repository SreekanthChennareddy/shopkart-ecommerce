package com.shopkart.product.service;

import com.shopkart.common.dto.PagedResponse;
import com.shopkart.common.exception.ResourceNotFoundException;
import com.shopkart.product.dto.ProductImageDto;
import com.shopkart.product.dto.ProductRequest;
import com.shopkart.product.dto.ProductResponse;
import com.shopkart.product.dto.ProductVariantDto;
import com.shopkart.product.entity.Category;
import com.shopkart.product.entity.Product;
import com.shopkart.product.entity.ProductImage;
import com.shopkart.product.entity.ProductVariant;
import com.shopkart.product.repository.CategoryRepository;
import com.shopkart.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request, UUID sellerId) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId().toString()));

        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .brand(request.getBrand())
                .category(category)
                .sellerId(sellerId)
                .build();

        List<ProductVariant> variants = new ArrayList<>();

        if (request.getVariants() != null) {
            for (ProductVariantDto vDto : request.getVariants()) {
                ProductVariant variant = ProductVariant.builder()
                        .product(product)
                        .sku(vDto.getSku())
                        .color(vDto.getColor())
                        .size(vDto.getSize())
                        .price(vDto.getPrice())
                        .sellingPrice(vDto.getSellingPrice())
                        .stockQuantity(vDto.getStockQuantity())
                        .build();

                // Map multiple images
                List<ProductImage> images = new ArrayList<>();
                if (vDto.getImages() != null) {
                    for (ProductImageDto imgDto : vDto.getImages()) {
                        images.add(ProductImage.builder()
                                .variant(variant)
                                .imageUrl(imgDto.getImageUrl())
                                .altText(imgDto.getAltText())
                                .isPrimary(imgDto.isPrimary())
                                .displayOrder(imgDto.getDisplayOrder())
                                .build());
                    }
                }
                variant.setImages(images);
                variants.add(variant);
            }
        }

        product.setVariants(variants);
        Product savedProduct = productRepository.save(product);
        log.info("Created product '{}' with {} variants", savedProduct.getTitle(), variants.size());

        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id.toString()));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = productRepository.findAll(pageable);
        List<ProductResponse> content = productPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PagedResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements((int) productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    private ProductResponse mapToResponse(Product product) {
        List<ProductVariantDto> variantDtos = product.getVariants().stream().map(v -> {
            List<ProductImageDto> imageDtos = v.getImages() != null
                    ? v.getImages().stream().map(img -> ProductImageDto.builder()
                    .id(img.getId())
                    .imageUrl(img.getImageUrl())
                    .altText(img.getAltText())
                    .isPrimary(img.isPrimary())
                    .displayOrder(img.getDisplayOrder())
                    .build()).collect(Collectors.toList())
                    : List.of();

            return ProductVariantDto.builder()
                    .id(v.getId())
                    .sku(v.getSku())
                    .color(v.getColor())
                    .size(v.getSize())
                    .price(v.getPrice())
                    .sellingPrice(v.getSellingPrice())
                    .stockQuantity(v.getStockQuantity())
                    .images(imageDtos)
                    .build();
        }).collect(Collectors.toList());

        return ProductResponse.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .brand(product.getBrand())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .sellerId(product.getSellerId())
                .active(product.isActive())
                .variants(variantDtos)
                .createdAt(product.getCreatedAt())
                .build();
    }
}