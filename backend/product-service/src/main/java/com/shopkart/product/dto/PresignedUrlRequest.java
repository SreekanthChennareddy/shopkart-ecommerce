package com.shopkart.product.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PresignedUrlRequest {

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "Content type is required (e.g. image/jpeg, image/png)")
    private String contentType;

    private String folder = "products"; // Default folder prefix
}