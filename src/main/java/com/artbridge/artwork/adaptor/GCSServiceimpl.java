package com.artbridge.artwork.adaptor;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class GCSServiceimpl implements GCSService {

    private final String bucketName;
    private final Storage storage;

    public GCSServiceimpl(@Value("${spring.cloud.gcp.storage.bucket}") String bucketName, Storage storage) {
        this.bucketName = bucketName;
        this.storage = storage;
    }

    @Override
    public String uploadImageToGCS(MultipartFile imageFile) throws IOException {
        String objectName = generateRandomObjectName();

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
        storage.create(blobInfo, imageFile.getBytes());

        return generateDownloadURL(objectName);
    }

    private String generateRandomObjectName() {
        return UUID.randomUUID() + ".jpg";
    }

    private String generateDownloadURL(String objectName) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }
}
