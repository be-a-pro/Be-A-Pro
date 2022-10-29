package com.beer.BeAPro.Dto;

import com.beer.BeAPro.Domain.UserInterestKeyword;
import com.beer.BeAPro.Domain.UserPosition;
import com.beer.BeAPro.Domain.UserTool;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUpAdditionalInfoDto { // 회원가입 절차 중 추가 정보 // 저장할 때 사용
        private Boolean mobileIsPublic = false;
        private List<UserPosition> userPositions = null;
        private List<UserInterestKeyword> userInterestKeywords = null;
        private List<UserTool> userTools = null;
        private Boolean portfolioIsPublic = true;
        private String portfolioLinks = null; // 3개 이하, 구분자 사용 "link1,link2,link3"

        @Builder
        public SignUpAdditionalInfoDto (Boolean mobileIsPublic,
                                               List<UserPosition> userPositions,
                                               List<UserInterestKeyword> userInterestKeywords,
                                               List<UserTool> userTools,
                                               Boolean portfolioIsPublic,
                                               String portfolioLinks){
            this.mobileIsPublic = mobileIsPublic;
            this.userPositions = userPositions;
            this.userInterestKeywords = userInterestKeywords;
            this.userTools = userTools;
            this.portfolioIsPublic = portfolioIsPublic;
            this.portfolioLinks = portfolioLinks;
        }
    }
}
