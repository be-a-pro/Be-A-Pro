package com.beer.BeAPro.Dto;

import com.beer.BeAPro.Domain.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
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
}
