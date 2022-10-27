package com.beer.BeAPro.Dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileUploadDto {

    private String originalName;
    private String modifiedName;
    private long size; // byte
    private String filepath;

    @Builder
    public FileUploadDto(String originalName, String modifiedName, long size, String filepath) {
        this.originalName = originalName;
        this.modifiedName = modifiedName;
        this.size = size;
        this.filepath = filepath;
    }
}
