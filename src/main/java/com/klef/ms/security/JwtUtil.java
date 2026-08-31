package com.klef.ms.security;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil 
{
    @Value("${jwt.secret}")
    private String SECRET;

        @Value("${jwt.expiration}")
        private long expiration;

    // Generate JWT
    public String generateToken(UserDetails userDetails) 
    {
        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .setId(UUID.randomUUID().toString())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username/email
    public String extractUsername(String token) 
    {
        return getClaims(token).getSubject();
    }

    // Validate JWT
    public boolean validateToken(
            String token,
            UserDetails userDetails) 
    {
        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    // Check expiration
    private boolean isTokenExpired(String token) 
    {
        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // Extract claims
    private Claims getClaims(String token) 
    {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(30)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Create signing key
    private Key getSigningKey() 
    {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes()
        );
    }
}