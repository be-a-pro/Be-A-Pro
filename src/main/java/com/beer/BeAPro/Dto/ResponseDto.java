package com.beer.BeAPro.Dto;

import com.beer.BeAPro.Domain.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class ResponseDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CheckTemporaryProjectDto {
        @NotBlank
        private String temporaryId;

        @Builder
        public CheckTemporaryProjectDto(String temporaryId) {
            this.temporaryId = temporaryId;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SaveProjectDto {
        @NotBlank
        private Long projectId;
        private String temporaryId;

        @Builder
        public SaveProjectDto(Long projectId, String temporaryId) {
            this.projectId = projectId;
            this.temporaryId = temporaryId;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PositionDto {
        @NotBlank
        private Category category;
        private Development development;
        private Design design;
        private Planning planning;
        private Etc etc;

        @Builder
        public PositionDto(Category category, Development development, Design design, Planning planning, Etc etc) {
            this.category = category;
            this.development = development;
            this.design = design;
            this.planning = planning;
            this.etc = etc;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ImageDto {
        private String filepath = null;
        private String originalName = null;

        @Builder
        public ImageDto(String filepath, String originalName) {
            this.filepath = filepath;
            this.originalName = originalName;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetProjectDto {
        @NotBlank
        private String title;
        private ImageDto projectImage = null;
        @NotEmpty
        private List<String> projectHashtags;
        @NotBlank
        private String kakaoLink;
        @NotBlank
        private String info;
        private String freeInfo = null;
        @NotBlank
        private String progressMethod;
        @NotEmpty
        private List<String> usedStacks;
        @Size(max = 3)
        private List<String> referenceLinks = null;
        @NotEmpty
        private List<PositionDto> projectPositions;
        @NotEmpty
        private List<Long> currentCountPerPosition; // 현재 인원
        @NotEmpty
        private List<Long> closingCountPerPosition; // 마감 인원

        @Builder
        public GetProjectDto(String title,
                             ImageDto projectImage,
                             List<String> projectHashtags,
                             String kakaoLink,
                             String info,
                             String freeInfo,
                             String progressMethod,
                             List<String> usedStacks,
                             List<String> referenceLinks,
                             List<PositionDto> projectPositions,
                             List<Long> currentCountPerPosition,
                             List<Long> closingCountPerPosition) {
            this.title  = title;
            this.projectImage = projectImage;
            this.projectHashtags = projectHashtags;
            this.kakaoLink = kakaoLink;
            this.info = info;
            this.freeInfo = freeInfo;
            this.progressMethod = progressMethod;
            this.usedStacks = usedStacks;
            this.referenceLinks = referenceLinks;
            this.projectPositions = projectPositions;
            this.currentCountPerPosition = currentCountPerPosition;
            this.closingCountPerPosition = closingCountPerPosition;
        }
    }
}
