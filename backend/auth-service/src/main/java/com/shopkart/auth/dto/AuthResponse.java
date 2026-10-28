package com.shopkart.auth.dto;

import com.shopkart.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;          // The short-lived Access Token (JWT)
    private String refreshToken;   // The long-lived Refresh Token (UUID or long JWT)
    @Builder.Default
    private String tokenType = "Bearer";
    private UUID userId;
    private String email;
    private String firstName;
    private Role role;
    private String assignedShard; // Useful to see which shard the user is on!
}
