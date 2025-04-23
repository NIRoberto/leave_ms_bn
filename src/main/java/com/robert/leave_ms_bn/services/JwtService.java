package com.robert.leave_ms_bn.services;

import com.robert.leave_ms_bn.entities.Role;
import com.robert.leave_ms_bn.entities.User;
import com.robert.leave_ms_bn.repositories.RoleRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {


    private final RoleRepository roleRepository;


    @Value("${spring.jwt.secret}")
    private String secret;

    public JwtService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public String generateToken(User user) {
        return generateAccessToken(user);
    }


    public String generateRefreshToken(User user) {
        return generateAccessToken(user);
    }



    private String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roleId", user.getRole().getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 day
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            var authHeader   =  token.replace("Bearer ", "");
            return getClaims(authHeader).getExpiration().after(new Date());

        } catch (JwtException e) {
            return false;
        }
    }

    public  Long getUserId(String token) {
          return Long.valueOf(getClaims(token).getSubject());
    }

    public String getUserRoleFromToken(String token) {
        var roleId = getClaims(token).get("roleId", Long.class);
        if (roleId == null) {
            throw new RuntimeException("Role ID not found in token");
        }
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found for ID: " + roleId));
        return role.getName();
    }


    public Claims getClaims(String token){
        return     Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}