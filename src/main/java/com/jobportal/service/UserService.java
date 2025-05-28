package com.jobportal.service;

import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            String blobPath = azureBlobService.uploadFileAndReturnBlobPath(file, "profile-pictures");
            user.setProfilePicture(blobPath);
            userRepository.save(user);
            return ResponseEntity.ok(blobPath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    public ResponseEntity<InputStreamResource> getProfilePicture(String filename) {
        try {
            String blobName = "profile-pictures/" + filename;
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(azureBlobService.getConnectionString())
                    .buildClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(azureBlobService.getContainerName());
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            BlobProperties properties = blobClient.getProperties();
            String contentType = properties.getContentType();
            InputStream is = blobClient.openInputStream();

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic()) // Add this line for caching
                .body(new InputStreamResource(is));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}