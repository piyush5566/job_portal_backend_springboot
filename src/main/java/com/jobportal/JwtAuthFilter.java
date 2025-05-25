package com.jobportal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String SECRET_KEY = "replace_this_with_a_strong_secret";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Allow CORS preflight requests to pass through (must be first)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");
        // Allow unauthenticated POST /users for registration
        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().equals("/users")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(SECRET_KEY)
                        .parseClaimsJws(token)
                        .getBody();
                // Optionally set user info in request attribute for controllers
                request.setAttribute("claims", claims);
            } catch (Exception e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid or expired token");
                return;
            }
        } else if (requiresAuth(request)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing Authorization header");
            return;
        }
        filterChain.doFilter(request, response);
    }

    // Only protect /jobs, /applications, /users (not /auth/*)
    private boolean requiresAuth(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/jobs") || path.startsWith("/applications") || path.startsWith("/users");
    }
}
