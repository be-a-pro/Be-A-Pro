package com.beer.BeAPro.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 사용자

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_image_id", unique = true)
    private ProjectImage projectImage; // 대표 이미지

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectHashtag> projectHashtags = new ArrayList<>(); // 해시태그

    private String kakaoLink; // 오픈 카카오톡 링크

    private String info; // 소개

    private String freeInfo; // 자유 양식

    private String progressMethod; // 진행 방식

    private String usedStacks; // 사용 프로그램 및 언어 // 구분자 사용 "stack1, stack2, stack3, ..."

    private String referenceLinks; // 참고 링크 // 3개 이하, 구분자 사용 "link1,link2,link3"

    private Boolean isApplyPossible; // 지원 가능 여부

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectPosition> projectPositions = new ArrayList<>(); // 모집 포지션

    private int views; // 조회수

    private Boolean isTemporary; // 임시 저장 여부

    private LocalDateTime restorationDate; // 복구 가능 날짜

}
