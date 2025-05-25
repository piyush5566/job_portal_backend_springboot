package com.jobportal;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jobportal.User;
import com.jobportal.UserRepository;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:8082", "http://localhost:3000"})
public class AuthController {
    private static final String SECRET_KEY = "replace_this_with_a_strong_secret_key_that_is_at_least_32_bytes_long";
    private static final long EXPIRATION_TIME = 86400000; // 1 day in ms

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(loginRequest.getEmail()) && u.getPassword().equals(loginRequest.getPassword()))
                .findFirst();
        if (userOpt.isPresent()) {
            byte[] keyBytes = SECRET_KEY.getBytes();
            String token = Jwts.builder()
                    .setSubject(userOpt.get().getEmail())
                    .claim("role", userOpt.get().getRole())
                    .claim("username", userOpt.get().getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes))
                    .compact();
            return ResponseEntity.ok(new JwtResponse(token));
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    // DTOs
    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    public static class JwtResponse {
        private String token;
        public JwtResponse(String token) { this.token = token; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
