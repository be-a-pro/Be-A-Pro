package com.beer.BeAPro.Dto;

import com.beer.BeAPro.Domain.ProjectHashtag;
import com.beer.BeAPro.Domain.ProjectImage;
import com.beer.BeAPro.Domain.ProjectPosition;
import com.beer.BeAPro.Domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProjectDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateDto { // 프로젝트 생성
        private User user;

        @Builder
        public CreateDto(User user) {
            this.user = user;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SaveDataDto { // 데이터 저장
        private String title;
        private ProjectImage projectImage;
        private List<ProjectHashtag> projectHashtags;
        private String kakaoLink;
        private String info;
        private String freeInfo = null;
        private String progressMethod;
        private String usedStacks;
        private String referenceLinks = null;
        private List<ProjectPosition> projectPositions;
        private Boolean isTemporary = false;

        @Builder
        public SaveDataDto(String title,
                           ProjectImage projectImage,
                           List<ProjectHashtag> projectHashtags,
                           String kakaoLink,
                           String info,
                           String freeInfo,
                           String progressMethod,
                           String usedStacks,
                           String referenceLinks,
                           List<ProjectPosition> projectPositions,
                           Boolean isTemporary) {
            this.title = title;
            this.projectImage = projectImage;
            this.projectHashtags = projectHashtags;
            this.kakaoLink = kakaoLink;
            this.info = info;
            this.freeInfo = freeInfo;
            this.progressMethod = progressMethod;
            this.usedStacks = usedStacks;
            this.referenceLinks = referenceLinks;
            this.projectPositions = projectPositions;
            this.isTemporary = isTemporary;
        }
    }
}
