package com.jobportal.service;

import com.jobportal.dto.JwtResponse;
import com.jobportal.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import com.jobportal.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration.ms}")
    private long expirationTime;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> login(String email, String password) {
        // Use the more efficient findByEmail method
        Optional<User> userOpt = Optional.ofNullable(userRepository.findByEmail(email));
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            System.out.println("AuthService - User Profile Picture from DB: " + userOpt.get().getProfilePicture());
// ... rest of the token building
            byte[] keyBytes = secretKey.getBytes();
            String token = Jwts.builder()
                    .setSubject(userOpt.get().getEmail())
                    .claim("role", userOpt.get().getRole())
                    .claim("username", userOpt.get().getUsername())
                    .claim("profilePicture", userOpt.get().getProfilePicture())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes))
                    .compact();
            return ResponseEntity.ok(new JwtResponse(token));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}