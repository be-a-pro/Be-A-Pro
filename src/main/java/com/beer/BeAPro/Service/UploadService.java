package com.beer.BeAPro.Service;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.InputStream;

public interface UploadService {
    void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName);
    void deleteFile(String filename);
    String getFileUrl(String filename);
}
