package com.beer.BeAPro.Domain;

import com.beer.BeAPro.Dto.FileUploadDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_file_id")
    private Long id;

    private String originalName;

    private String modifiedName;

    private long size; // byte

    private String filepath;


    // == 생성 메서드 == //
    public static PortfolioFile createProjectImage(FileUploadDto fileUploadDto) {
        PortfolioFile portfolioFile = new PortfolioFile();

        portfolioFile.originalName = fileUploadDto.getOriginalName();
        portfolioFile.modifiedName = fileUploadDto.getModifiedName();
        portfolioFile.size = fileUploadDto.getSize();
        portfolioFile.filepath = fileUploadDto.getFilepath();

        return portfolioFile;
    }
}
