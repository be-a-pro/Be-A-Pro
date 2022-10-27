package com.beer.BeAPro.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;


@Component
@RequiredArgsConstructor
public class AwsS3UploadService implements UploadService {

    private final AmazonS3 amazonS3;
    private final S3Component s3Component;

    @Override
    public String getFileUrl(String filename) {
        return amazonS3.getUrl(s3Component.getBucket(), filename).toString();
    }

    @Override
    public void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName) {
        amazonS3.putObject(new PutObjectRequest(s3Component.getBucket(), fileName, inputStream, objectMetadata));
    }
}
