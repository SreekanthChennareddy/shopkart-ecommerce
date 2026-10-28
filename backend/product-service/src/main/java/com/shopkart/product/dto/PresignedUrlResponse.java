package com.shopkart.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlResponse {

    // The temporary signed URL where the browser makes an HTTP PUT request:
    private String uploadUrl;

    // The unique S3 key generated for this file:
    private String fileKey;

    // The permanent public URL to store in the database:
    private String publicUrl;

    // Expiry time in seconds:
    private long expiresInSeconds;
}