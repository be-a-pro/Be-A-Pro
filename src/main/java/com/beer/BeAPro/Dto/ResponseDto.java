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
    public static class GetProjectIdDto {
        @NotBlank
        private Long projectId;

        @Builder
        public GetProjectIdDto(Long projectId) {
            this.projectId = projectId;
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
    public static class GetProjectDataDto {
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
        private List<Boolean> isApplicants = null; // 지원자 있는지 여부

        @Builder
        public GetProjectDataDto(String title,
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
                                 List<Long> closingCountPerPosition,
                                 List<Boolean> isApplicants) {
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
            this.isApplicants = isApplicants;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectWriterDto { // 프로젝트 상세 페이지에서 필요한 작성자 정보
        private String name;
        private String email;
        private ImageDto profileImage = null;

        @Builder
        public ProjectWriterDto(String name, String email, ImageDto profileImage) {
            this.name = name;
            this.email = email;
            this.profileImage = profileImage;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetProjectDetailDto {
        private GetProjectDataDto project;
        private ProjectWriterDto user = null;
        private String createdDateTime; // yyyy-MM-dd HH:mm:ss
        private Long views;
        private Boolean isApplyPossible;

        @Builder
        public GetProjectDetailDto(GetProjectDataDto project,
                                   ProjectWriterDto user,
                                   String createdDateTime,
                                   Long views,
                                   Boolean isApplyPossible) {
            this.project = project;
            this.user = user;
            this.createdDateTime = createdDateTime;
            this.views = views;
            this.isApplyPossible = isApplyPossible;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectDataOfProjectListDto { // 프로젝트 목록 페이지에서 보일 프로젝트 데이터
        @NotBlank
        private Long id;
        @NotBlank
        private String title;
        private ImageDto projectImage = null;
        @NotEmpty
        private List<PositionDto> projectPositions;
        @NotEmpty
        private List<Long> currentCountPerPosition; // 현재 인원
        @NotEmpty
        private List<Long> closingCountPerPosition; // 마감 인원

        @Builder
        public ProjectDataOfProjectListDto(Long id,
                                           String title,
                                           ImageDto projectImage,
                                           List<PositionDto> projectPositions,
                                           List<Long> currentCountPerPosition,
                                           List<Long> closingCountPerPosition) {
            this.id = id;
            this.title = title;
            this.projectImage = projectImage;
            this.projectPositions = projectPositions;
            this.currentCountPerPosition = currentCountPerPosition;
            this.closingCountPerPosition = closingCountPerPosition;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TotalDataOfProjectListDto { // 프로젝트 목록 페이지에서 보일 프로젝트, 작성자 등을 포함한 전체 데이터
        private ProjectDataOfProjectListDto project;
        private ProjectWriterDto user = null;
        private String createdDateTime; // yyyy-MM-dd HH:mm:ss
        private Long views;
        private Boolean isApplyPossible;

        @Builder
        public TotalDataOfProjectListDto(ProjectDataOfProjectListDto project,
                                         ProjectWriterDto user,
                                         String createdDateTime,
                                         Long views,
                                         Boolean isApplyPossible) {
            this.project = project;
            this.user = user;
            this.createdDateTime = createdDateTime;
            this.views = views;
            this.isApplyPossible = isApplyPossible;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetProjectListDto { // 프로젝트 목록 페이지 응답 DTO
        private List<TotalDataOfProjectListDto> projectList;
        private Boolean hasNext;

        @Builder
        public GetProjectListDto(List<TotalDataOfProjectListDto> projectList,
                                 Boolean hasNext) {
            this.projectList = projectList;
            this.hasNext = hasNext;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class GetProjectListInIndexDto { // Index 페이지 NEW 프로젝트 목록
        private List<TotalDataOfProjectListDto> projectList;

        @Builder
        public GetProjectListInIndexDto(List<TotalDataOfProjectListDto> projectList) {
            this.projectList = projectList;
        }
    }
}
