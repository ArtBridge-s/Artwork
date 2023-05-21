package com.artbridge.artwork.adaptor;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface GCSService {
    String uploadImageToGCS(MultipartFile imageFile) throws IOException;
}
