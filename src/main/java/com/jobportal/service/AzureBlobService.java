package com.jobportal.service;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class AzureBlobService {
    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name}")
    private String containerName;

    public String uploadFile(MultipartFile file, String folder) throws Exception {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        String filename = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(filename);
        try (InputStream is = file.getInputStream()) {
            blobClient.upload(is, file.getSize(), true);
        }
        return blobClient.getBlobUrl();
    }
}
