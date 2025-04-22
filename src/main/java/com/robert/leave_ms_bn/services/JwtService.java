package com.robert.leave_ms_bn.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${spring.jwt.secret}")
    private String secret;

    public String generateToken(String email) {
        System.out.println("Using secret: " + secret);
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 day
                .signWith(Keys.hmacShaKeyFor("W+ptkkoSFguUac5JraK5tBOXy0s3J9ktfiLMQj59fdM=".getBytes()))
                .compact();
    }
}