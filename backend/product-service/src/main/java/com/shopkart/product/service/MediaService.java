package com.shopkart.product.service;

import com.shopkart.common.exception.BadRequestException;
import com.shopkart.product.dto.PresignedUrlRequest;
import com.shopkart.product.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaService {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.cdn-url}")
    private String cdnUrl;

    // Allow only safe image types
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public PresignedUrlResponse generatePresignedUploadUrl(PresignedUrlRequest request) {
        // 1. Validate MIME type
        if (!ALLOWED_IMAGE_TYPES.contains(request.getContentType().toLowerCase())) {
            throw new BadRequestException("Invalid file type. Only JPEG, PNG, and WebP are allowed.");
        }

        // 2. Generate a collision-free S3 key: e.g. "products/a1b2c3d4-iphone15.png"
        String sanitizedFileName = request.getFileName().replaceAll("\\s+", "-").toLowerCase();
        String fileKey = String.format("%s/%s-%s",
                request.getFolder(),
                UUID.randomUUID(),
                sanitizedFileName);

        // 3. Define the S3 PUT object request parameters
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(request.getContentType())
                .build();

        // 4. Sign the request with a 5-minute expiration
        Duration expiration = Duration.ofMinutes(5);

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);
        String uploadUrl = presignedPutObjectRequest.url().toString();

        // 5. Construct public URL (CloudFront CDN or direct S3 URL)
        String publicUrl = String.format("%s/%s", cdnUrl.replaceAll("/+$", ""), fileKey);

        log.info("Generated S3 Pre-Signed upload URL for key: {}", fileKey);

        return PresignedUrlResponse.builder()
                .uploadUrl(uploadUrl)
                .fileKey(fileKey)
                .publicUrl(publicUrl)
                .expiresInSeconds(expiration.toSeconds())
                .build();
    }
}