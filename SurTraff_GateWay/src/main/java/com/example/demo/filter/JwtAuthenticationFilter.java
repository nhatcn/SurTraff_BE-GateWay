package com.example.demo.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String SECRET_KEY = "ZFF3NGJON0BqWDJ5WnNVOSEuTkYwbkE4anpUazlrUzFASFI1WHFUX3lXNnRGYjBsSGYj";

    public JwtAuthenticationFilter() {
        System.out.println("✅ JwtAuthenticationFilter constructor called");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        if (
                path.equals("/api/users/login") ||
                        path.equals("/api/users/register") ||
                        path.equals("/api/users/signin") ||
                        path.equals("/api/users/forgotPassword")
        ) {
            return chain.filter(exchange);
        }


        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        System.out.println("🔥 Jwt Filter path: " + path);
        System.out.println("🔥 Method: " + request.getMethod());
        System.out.println("🔥 Authorization: " + request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));

        try {
            String token = authHeader.substring(7);
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.getDecoder().decode(SECRET_KEY))
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("userId", userId)
                    .header("role", role)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
