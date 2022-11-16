package com.beer.BeAPro.Dto;

import com.beer.BeAPro.Domain.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        private Category category;
        private Development development;
        private Design design;
        private Planning planning;
        private Etc etc;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUpAdditionalInfoDto { // 회원가입 절차 중 추가 정보 입력
        @NotNull
        private Boolean mobileIsPublic;
        private List<PositionDto> userPositions;
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
        private List<Long> currentCountPerPosition; // 현재 인원
        @NotEmpty
        private List<Long> closingCountPerPosition; // 마감 인원 // 최대 10명
        @NotNull
        private Boolean isTemporary; // 임시저장 여부
    }
}
