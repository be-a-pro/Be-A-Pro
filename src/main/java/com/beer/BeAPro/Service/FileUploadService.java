package com.beer.BeAPro.Service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.beer.BeAPro.Domain.PortfolioFile;
import com.beer.BeAPro.Domain.ProfileImage;
import com.beer.BeAPro.Domain.ProjectImage;
import com.beer.BeAPro.Dto.FileUploadDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.PortfolioFileRepository;
import com.beer.BeAPro.Repository.ProfileImageRepository;
import com.beer.BeAPro.Repository.ProjectImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final AwsS3UploadService awsS3UploadService;
    private final ProfileImageRepository profileImageRepository;
    private final ProjectImageRepository projectImageRepository;
    private final PortfolioFileRepository portfolioFileRepository;

    private final String[] fileExtension = {".jpg", ".png", ".pdf", ".ppt", ".pptx", ".hwp", ".hwpx"};
    private final String[] imageExtension = {".png", ".jpg", ".jpeg", ".gif"};


    // 파일의 확장자 추출
    public String getFileExtension(String originalName) {
        try {
            return originalName.substring(originalName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new RestApiException(ErrorCode.INVALID_FILE_NAME); // 잘못된 형식의 파일명
        }
    }

    // 알맞은 확장자가 아닐 경우 false 반환
    public boolean checkExtension(String extension, String type) {
        if (type.equals("file")) {
            return Arrays.asList(fileExtension).contains(extension);
        } else if (type.equals("image")) {
            return Arrays.asList(imageExtension).contains(extension);
        } else {
            return false;
        }
    }

    // AWS S3에 파일 업로드
    @Transactional
    public FileUploadDto uploadFile(MultipartFile file, long sizeLimit, String type) {
        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            originalName = type;
        }

        String extension = getFileExtension(originalName);
        if (!checkExtension(extension, type)) {
            throw new RestApiException(ErrorCode.INVALID_EXTENSION);
        }

        String modifiedName =  UUID.randomUUID().toString().concat(extension);
        long size = file.getSize();
        if (size >= sizeLimit) {
            throw new RestApiException(ErrorCode.FILE_TOO_LARGE);
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        objectMetadata.setContentType(file.getContentType());
        try (InputStream inputStream = file.getInputStream()) {
            awsS3UploadService.uploadFile(inputStream, objectMetadata, modifiedName);
        } catch (IOException e) {
            log.error("An error occurred during file conversion.");
            throw new RuntimeException("Error not defined.");
        }

        return FileUploadDto.builder()
                .originalName(originalName)
                .modifiedName(modifiedName)
                .size(size)
                .filepath(awsS3UploadService.getFileUrl(modifiedName))
                .build();
    }

    // AWS S3에서 파일 삭제
    @Transactional
    public void deleteFile(String fileName) {
        awsS3UploadService.deleteFile(fileName);
    }


    // ===== DB에 저장 ===== //

    @Transactional
    public ProfileImage saveProfileImage(FileUploadDto fileUploadDto) {
        ProfileImage profileImage = ProfileImage.createProjectImage(fileUploadDto);
        profileImageRepository.save(profileImage);
        return profileImage;
    }

    @Transactional
    public ProjectImage saveProjectImage(FileUploadDto fileUploadDto) {
        ProjectImage projectImage = ProjectImage.createProjectImage(fileUploadDto);
        projectImageRepository.save(projectImage);
        return projectImage;
    }

    @Transactional
    public PortfolioFile savePortfolioFile(FileUploadDto fileUploadDto) {
        PortfolioFile portfolioFile = PortfolioFile.createProjectImage(fileUploadDto);
        portfolioFileRepository.save(portfolioFile);
        return portfolioFile;
    }


    // ===== DB에서 삭제 ===== //

    @Transactional
    public void deleteProfileImage(ProfileImage profileImage) {
        profileImageRepository.delete(profileImage);
    }

    @Transactional
    public void deleteProjectImage(ProjectImage projectImage) {
        projectImageRepository.delete(projectImage);
    }

    @Transactional
    public void deletePortfolioFile(PortfolioFile portfolioFile) {
        portfolioFileRepository.delete(portfolioFile);
    }
}
