package com.jobportal.service;

import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AzureBlobService azureBlobService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public ResponseEntity<User> getProfile(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<User> updateProfile(UserDetails userDetails, User updated) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        user.setUsername(updated.getUsername());
        user.setEmail(updated.getEmail());
        if (updated.getProfilePicture() != null) user.setProfilePicture(updated.getProfilePicture());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<String> uploadProfilePicture(UserDetails userDetails, MultipartFile file) {
        try {
            User user = userRepository.findByEmail(userDetails.getUsername());
            String url = azureBlobService.uploadFile(file, "profile-pictures");
            user.setProfilePicture(url);
            userRepository.save(user);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }
}