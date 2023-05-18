package com.artbridge.artwork.adaptor;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class GCSServiceimpl implements GCSService {
    @Override
    public String uploadFileToGCS(MultipartFile file) throws IOException {
        String bucketName = "artbridge-bucket";

        // Initialize a Cloud Storage client
        Storage storage = StorageOptions.newBuilder()
            .setCredentials(GoogleCredentials.fromStream(new FileInputStream("/artwork")))
            .build()
            .getService();

        // Convert MultipartFile to java.nio.file.Path
        Path tempFile = Files.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);

        // Generate a random objectName
        String objectName = UUID.randomUUID().toString();

        // Upload the file to Cloud Storage
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(tempFile));

        // Return the public download URL
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }
}
