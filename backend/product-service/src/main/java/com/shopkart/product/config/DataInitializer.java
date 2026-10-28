package com.shopkart.product.config;

import com.shopkart.product.entity.Category;
import com.shopkart.product.entity.Product;
import com.shopkart.product.entity.ProductImage;
import com.shopkart.product.entity.ProductVariant;
import com.shopkart.product.repository.CategoryRepository;
import com.shopkart.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // 1. Ensure Categories Exist
        if (categoryRepository.count() == 0) {
            log.info("Seeding initial Flipkart category taxonomy...");

            Category mobiles = Category.builder()
                    .name("Mobiles & Tablets")
                    .slug("mobiles-tablets")
                    .iconUrl("https://rukminim2.flixcart.com/flap/128/128/image/22fddf3c7da4c4f4.png")
                    .build();

            Category electronics = Category.builder()
                    .name("Electronics")
                    .slug("electronics")
                    .iconUrl("https://rukminim2.flixcart.com/flap/128/128/image/69c6589653afdb9a.png")
                    .build();

            Category fashion = Category.builder()
                    .name("Fashion")
                    .slug("fashion")
                    .iconUrl("https://rukminim2.flixcart.com/fk-p-flap/128/128/image/0d750caea582d3f4.png")
                    .build();

            Category home = Category.builder()
                    .name("Home & Furniture")
                    .slug("home-furniture")
                    .iconUrl("https://rukminim2.flixcart.com/flap/128/128/image/ab7e2b022a4587dd.jpg")
                    .build();

            Category appliances = Category.builder()
                    .name("Appliances")
                    .slug("appliances")
                    .iconUrl("https://rukminim2.flixcart.com/flap/128/128/image/013a73d9d60dd36e.jpg")
                    .build();

            categoryRepository.saveAll(List.of(mobiles, electronics, fashion, home, appliances));
            log.info("Successfully seeded 5 root Flipkart categories!");
        }

        if (productRepository.count() == 0) {
            log.info("No products found. Fetching categories from DB to attach products...");

            List<Category> allCategories = categoryRepository.findAll();
            log.info("Found {} categories in DB", allCategories.size());

            if (!allCategories.isEmpty()) {
                Category cat1 = allCategories.get(0);
                Category cat2 = (allCategories.size() > 1) ? allCategories.get(1) : cat1;

                UUID dummySellerId = UUID.randomUUID();

                // Product 1: Apple iPhone 15
                Product iphone15 = Product.builder()
                        .title("Apple iPhone 15 (128 GB) - Black")
                        .description("Dynamic Island, 48MP Main Camera with 2x Telephoto, and USB-C.")
                        .brand("Apple")
                        .category(cat1)
                        .sellerId(dummySellerId)
                        .build();

                ProductVariant iphoneVariant = ProductVariant.builder()
                        .product(iphone15)
                        .sku("IPH15-BLK-128")
                        .color("Black")
                        .size("128GB")
                        .price(new BigDecimal("79900.00"))
                        .sellingPrice(new BigDecimal("69999.00"))
                        .stockQuantity(50)
                        .build();

                List<ProductImage> iphoneImages = List.of(
                        ProductImage.builder()
                                .variant(iphoneVariant)
                                .imageUrl("https://rukminim2.flixcart.com/image/832/832/xif0q/mobile/h/d/9/-original-imagtc2qzgnnuhxh.jpeg")
                                .altText("iPhone 15 Black Front")
                                .isPrimary(true)
                                .displayOrder(1)
                                .build()
                );
                iphoneVariant.setImages(iphoneImages);
                iphone15.setVariants(List.of(iphoneVariant));

                // Product 2: Apple MacBook Air M2
                Product macbook = Product.builder()
                        .title("Apple MacBook Air M2 - (8 GB/256 GB SSD) Space Grey")
                        .description("M2 chip with next-generation CPU, GPU, and ML performance.")
                        .brand("Apple")
                        .category(cat2)
                        .sellerId(dummySellerId)
                        .build();

                ProductVariant macbookVariant = ProductVariant.builder()
                        .product(macbook)
                        .sku("MBA-M2-GRY-256")
                        .color("Space Grey")
                        .size("256GB")
                        .price(new BigDecimal("99900.00"))
                        .sellingPrice(new BigDecimal("84990.00"))
                        .stockQuantity(25)
                        .build();

                List<ProductImage> macImages = List.of(
                        ProductImage.builder()
                                .variant(macbookVariant)
                                .imageUrl("https://rukminim2.flixcart.com/image/832/832/xif0q/computer/2/v/v/-original-imagfdeqter4sj2j.jpeg")
                                .altText("MacBook Air M2")
                                .isPrimary(true)
                                .displayOrder(1)
                                .build()
                );
                macbookVariant.setImages(macImages);
                macbook.setVariants(List.of(macbookVariant));

                productRepository.saveAll(List.of(iphone15, macbook));
                log.info(">>> SUCCESS: Seeded iPhone 15 and MacBook Air into PostgreSQL!");
            }
        }
    }
}