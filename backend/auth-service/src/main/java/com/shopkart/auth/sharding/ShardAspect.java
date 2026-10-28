package com.shopkart.auth.sharding;


import com.shopkart.auth.dto.LoginRequest;
import com.shopkart.auth.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // MUST RUN BEFORE @Transactional!
public class ShardAspect {

    @Around("@annotation(WithShard)")
    public Object routeShard(ProceedingJoinPoint joinPoint) throws Throwable {
        String email = null;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof RegisterRequest reg) {
                email = reg.getEmail();
                break;
            } else if (arg instanceof LoginRequest login) {
                email = login.getEmail();
                break;
            }
        }
        ShardType shard = ShardContextHolder.determineShardByEmail(email);
        ShardContextHolder.setShard(shard);
        log.info(">>> [SHARD ROUTER] Switched to {} for email '{}'", shard, email);
        try {
            return joinPoint.proceed();
        } finally {
            ShardContextHolder.clear();
            log.info(">>> [SHARD ROUTER] Cleared context");
        }
    }
}
