package com.beer.BeAPro.Dto;

import com.beer.BeAPro.Domain.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class RequestDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PositionDto {
        @NotBlank
        @ApiModelProperty(example = "DEVELOPMENT") // swagger 예시
        private Category category;
        @ApiModelProperty(example = "AI")
        private Development development;
        @ApiModelProperty(hidden = true) // swagger 예시
        private Design design;
        @ApiModelProperty(hidden = true) // swagger 예시
        private Planning planning;
        @ApiModelProperty(hidden = true) // swagger 예시
        private Etc etc;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateUserPositionDto {
        @NotBlank
        private PositionDto position;
        @NotNull
        private Boolean isRepresentative;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUpAdditionalInfoDto { // 회원가입 절차 중 추가 정보 입력
        @NotNull
        private Boolean mobileIsPublic;
        @Size(min = 1) // 대표 포지션
        private List<CreateUserPositionDto> userPositions;
        private List<String> userInterestKeywords;
        private List<String> userTools;
        @NotNull
        private Boolean portfolioIsPublic;
        @Size(max = 3)
        private List<String> portfolioLinks;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectDto { // 프로젝트 생성 및 데이터 저장
        @NotBlank
        private String title;
        @NotEmpty
        private List<String> projectHashtags;
        @NotBlank
        private String kakaoLink;
        @NotBlank
        private String info;
        private String freeInfo;
        @NotBlank
        private String progressMethod;
        @NotEmpty
        private List<String> usedStacks;
        @Size(max = 3)
        private List<String> referenceLinks;
        @NotEmpty
        private List<PositionDto> projectPositions;
        @NotEmpty
        private List<Long> closingCountPerPosition; // 마감 인원 // 최대 10명
        @NotNull
        private Boolean isTemporary; // 임시저장 여부
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ApplyDto { // 프로젝트 지원
        @NotBlank
        private Long projectId;
        @NotBlank
        private PositionDto position;
    }
}
