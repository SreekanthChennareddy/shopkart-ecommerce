package com.shopkart.auth.repository;

import com.shopkart.auth.entity.User;
import com.shopkart.auth.entity.UserSessionToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionTokenRepository extends JpaRepository<UserSessionToken, UUID> {

    Optional<UserSessionToken> findByRefreshToken(String token);

    @Modifying
    @Query("DELETE FROM UserSessionToken t WHERE t.user = :user")
    void deleteByUser(User user);

    Optional<UserSessionToken> findByUser(User user);
}
