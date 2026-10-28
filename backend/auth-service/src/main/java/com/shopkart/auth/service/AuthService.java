package com.shopkart.auth.service;

import com.shopkart.auth.dto.*;
import com.shopkart.auth.entity.User;
import com.shopkart.auth.entity.UserSessionToken;
import com.shopkart.auth.repository.UserRepository;
import com.shopkart.auth.repository.UserSessionTokenRepository;
import com.shopkart.auth.security.JwtUtils;
import com.shopkart.auth.sharding.ShardContextHolder;
import com.shopkart.auth.sharding.WithShard;
import com.shopkart.common.exception.BadRequestException;
import com.shopkart.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserSessionTokenRepository sessionTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @WithShard // 1. Sets shard in ThreadLocal BEFORE @Transactional opens connection!
    @Transactional
    public AuthResponse register(RegisterRequest request){

        String currentShard = ShardContextHolder.getShard().name();
        log.info("Routing REGISTER for email '{}' to {}", request.getEmail(), currentShard);

        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email is already registered: " + request.getEmail());
            }

            User user = User.builder()
                    .email(request.getEmail().trim().toLowerCase())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .phone(request.getPhone())
                    .role(request.getRole())
                    .build();
            User savedUser = userRepository.save(user);

            // 1. Generate short-lived Access Token (15 mins)
            String token = jwtUtils.generateToken(savedUser, currentShard);

            // 2. Generate and store long-lived Refresh Token (7 days) in the user's shard DB!
            UserSessionToken sessionToken = createOrUpdateSessionToken(savedUser);

            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(sessionToken.getRefreshToken())
                    .userId(savedUser.getId())
                    .email(savedUser.getEmail())
                    .firstName(savedUser.getFirstName())
                    .role(savedUser.getRole())
                    .assignedShard(currentShard)
                    .build();
        } finally {
            ShardContextHolder.clear();
        }

    }

    @WithShard // 1. Sets shard in ThreadLocal BEFORE @Transactional opens connection!
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        String currentShard = ShardContextHolder.getShard().name();
        log.info("Routing LOGIN for email '{}' to {}", request.getEmail(), currentShard);
        try {
            User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadRequestException("Invalid email or password");
            }

            // 1. Generate short-lived Access Token (15 mins)
            String token = jwtUtils.generateToken(user, currentShard);
            // 2. Generate / Rotate long-lived Refresh Token (7 days) in DB!
            UserSessionToken sessionToken = createOrUpdateSessionToken(user);

            return AuthResponse.builder()
                    .token(token)
                    .refreshToken(sessionToken.getRefreshToken())
                    .userId(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .role(user.getRole())
                    .assignedShard(currentShard)
                    .build();
        } finally {
            ShardContextHolder.clear();
        }
    }


    @WithShard
    @Transactional
    public TokenRefreshResponse refreshAccessToken(TokenRefreshRequest request) {
        String currentShard = ShardContextHolder.getShard().name();

        // 1. Locate token within the routed shard
         UserSessionToken session = sessionTokenRepository
                .findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired refresh token"));

         // 2. Validate expiration and status
        if (!session.isActive() || session.getExpiryTime().isBefore(Instant.now())) {
            sessionTokenRepository.delete(session);
            throw new BadRequestException("Refresh token has expired or is inactive. Please log in again.");
        }
        // 3. Issue fresh access token (and optionally rotate the refresh token)
        User user = session.getUser();

        // 3.1. Generate fresh Access Token (JWT)
        String newAccessToken = jwtUtils.generateToken(user, currentShard);

        // 3.2. Generate fresh refresh Access Token (JWT)
        String newRefreshToken = jwtUtils.generateOpaqueRefreshToken();
        session.setRefreshToken(newRefreshToken);
        session.setExpiryTime(Instant.now().plus(7, ChronoUnit.DAYS));
        sessionTokenRepository.save(session);
        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private UserSessionToken createOrUpdateSessionToken(User user) {
        // Generate an opaque, cryptographically random token string
        String tokenValue = jwtUtils.generateOpaqueRefreshToken();

        // 7 days expiration for refresh tokens
        Instant expiryTime = Instant.now().plus(7, ChronoUnit.DAYS);

        // Look for existing session or create a new one
        UserSessionToken session = sessionTokenRepository.findByUser(user)
                .orElse(UserSessionToken.builder()
                        .user(user)
                        .build());

        session.setRefreshToken(tokenValue);
        session.setExpiryTime(expiryTime);
        session.setActive(true);

        return sessionTokenRepository.save(session);
    }
}