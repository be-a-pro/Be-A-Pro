package com.beer.BeAPro.Domain;

import com.beer.BeAPro.Dto.FileUploadDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_image_id")
    private Long id;

    private String originalName;

    private String modifiedName;

    private long size; // byte

    private String filepath;


    // == 생성 메서드 == //
    public static ProjectImage createProjectImage(FileUploadDto fileUploadDto) {
        ProjectImage projectImage = new ProjectImage();

        projectImage.originalName = fileUploadDto.getOriginalName();
        projectImage.modifiedName = fileUploadDto.getModifiedName();
        projectImage.size = fileUploadDto.getSize();
        projectImage.filepath = fileUploadDto.getFilepath();

        return projectImage;
    }
}
