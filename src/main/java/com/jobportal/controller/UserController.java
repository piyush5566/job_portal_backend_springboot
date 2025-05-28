package com.jobportal.controller;

import com.jobportal.model.User;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getProfile(userDetails);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody User updated) {
        return userService.updateProfile(userDetails, updated);
    }

    @PostMapping("/profile/picture")
    public ResponseEntity<String> uploadProfilePicture(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("file") MultipartFile file) {
        return userService.uploadProfilePicture(userDetails, file);
    }

    @GetMapping("/profile/picture/{filename}")
    public ResponseEntity<InputStreamResource> getProfilePicture(@PathVariable String filename, @AuthenticationPrincipal UserDetails userDetails) {
        // Only allow access if user is authenticated
        if (userDetails == null) {
            return ResponseEntity.status(403).build();
        }
        return userService.getProfilePicture(filename);
    }
}
