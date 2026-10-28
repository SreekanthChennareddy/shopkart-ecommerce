package com.shopkart.product.controller;

import com.shopkart.common.dto.ApiResponse;
import com.shopkart.product.dto.PresignedUrlRequest;
import com.shopkart.product.dto.PresignedUrlResponse;
import com.shopkart.product.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<PresignedUrlResponse>> getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request) {

        PresignedUrlResponse response = mediaService.generatePresignedUploadUrl(request);
        return ResponseEntity.ok(ApiResponse.success("Pre-signed upload URL generated successfully", response));
    }
}