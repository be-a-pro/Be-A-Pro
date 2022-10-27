package com.beer.BeAPro.Domain;

import com.beer.BeAPro.Dto.FileUploadDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_image_id")
    private Long id;

    private String originalName;

    private String modifiedName;

    private long size; // byte

    private String filepath;


    // == 생성 메서드 == //
    public static ProfileImage createProjectImage(FileUploadDto fileUploadDto) {
        ProfileImage profileImage = new ProfileImage();

        profileImage.originalName = fileUploadDto.getOriginalName();
        profileImage.modifiedName = fileUploadDto.getModifiedName();
        profileImage.size = fileUploadDto.getSize();
        profileImage.filepath = fileUploadDto.getFilepath();

        return profileImage;
    }
}
