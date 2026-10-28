package com.shopkart.auth.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh token must not be empty")
    private String refreshToken;

    @NotBlank(message = "Email is required to locate the user's shard")
    private String email;
}
