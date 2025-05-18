package com.example.demo.util;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Base64;
import java.util.Date;
import java.util.List;

public class JwtUtil {

    private static final byte[] SECRET_KEY = Base64.getDecoder().decode("ZFF3NGJON0BqWDJ5WnNVOSEuTkYwbkE4anpUazlrUzFASFI1WHFUX3lXNnRGYjBsSGYj");

    public static String generateToken(String userId, String role) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)  // Thêm role vào token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 giờ hết hạn
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    // Validate JWT Token
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY) // Dùng Base64.decode()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract user ID from the JWT Token
    public static String extractUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY) // Dùng Base64.decode()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    public static List<GrantedAuthority> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();

        String role = claims.get("role", String.class);
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

}
