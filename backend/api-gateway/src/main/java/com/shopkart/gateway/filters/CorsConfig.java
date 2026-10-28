package com.shopkart.gateway.filters;

import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Component
public class CorsConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // Run before all other filters!
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allowed origins (React app)
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));

        // Allowed HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allowed Headers (include Authorization, Content-Type, and tracing headers)
        corsConfig.setAllowedHeaders(Collections.singletonList("*"));

        // Headers exposed to browser (so frontend can read X-Trace-Id)
        corsConfig.setExposedHeaders(Arrays.asList("Authorization", "X-Trace-Id", "X-Request-Id"));

        // Allow credentials (cookies / authorization headers)
        corsConfig.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        corsConfig.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }

}
