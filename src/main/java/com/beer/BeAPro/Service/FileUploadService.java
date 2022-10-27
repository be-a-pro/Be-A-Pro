package com.beer.BeAPro.Service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.beer.BeAPro.Domain.PortfolioFile;
import com.beer.BeAPro.Domain.ProfileImage;
import com.beer.BeAPro.Domain.Project;
import com.beer.BeAPro.Domain.ProjectImage;
import com.beer.BeAPro.Dto.FileUploadDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.PortfolioFileRepository;
import com.beer.BeAPro.Repository.ProfileImageRepository;
import com.beer.BeAPro.Repository.ProjectImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final AwsS3UploadService awsS3UploadService;
    private final ProfileImageRepository profileImageRepository;
    private final ProjectImageRepository projectImageRepository;
    private final PortfolioFileRepository portfolioFileRepository;


    // 파일의 확장자 추출
    public String getFileExtension(String originalName) {
        try {
            return originalName.substring(originalName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("잘못된 형식의 파일입니다.: " + originalName);
        }
    }

    // AWS S3에 파일 업로드
    @Transactional
    public FileUploadDto uploadFile(MultipartFile file, long sizeLimit) {
        String originalName = file.getOriginalFilename();
        String modifiedName =  UUID.randomUUID().toString().concat(getFileExtension(originalName));
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
            throw new IllegalArgumentException("파일 변환 중 에러가 발생했습니다.");
        }

        return FileUploadDto.builder()
                .originalName(originalName)
                .modifiedName(modifiedName)
                .size(size)
                .filepath(awsS3UploadService.getFileUrl(modifiedName))
                .build();
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
