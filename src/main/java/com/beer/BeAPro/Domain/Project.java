package com.beer.BeAPro.Domain;

import com.beer.BeAPro.Dto.ProjectDto;
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

    private String title; // 제목

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

    private Long views; // 조회수

    private Boolean isTemporary; // 임시 저장 여부

    private LocalDateTime restorationDate; // 복구 가능 날짜


    // == 생성 메서드 == //
    public static Project createProject(ProjectDto.CreateDto createDto) { // 생성
        Project project = new Project();

        project.user = createDto.getUser();

        project.views = 0L;
        project.isApplyPossible = true;

        return project;
    }

    public Project saveData(ProjectDto.SaveDataDto saveDataDto) { // 저장 또는 최초의 임시저장
        this.title = saveDataDto.getTitle();
        this.projectImage = saveDataDto.getProjectImage();
        this.projectHashtags = saveDataDto.getProjectHashtags();
        this.kakaoLink = saveDataDto.getKakaoLink();
        this.info = saveDataDto.getInfo();
        this.freeInfo = saveDataDto.getFreeInfo();
        this.progressMethod = saveDataDto.getProgressMethod();
        this.usedStacks = saveDataDto.getUsedStacks();
        this.referenceLinks = saveDataDto.getReferenceLinks();
        this.projectPositions = saveDataDto.getProjectPositions();
        this.isTemporary = saveDataDto.getIsTemporary();

        return this;
    }

    public Project update(ProjectDto.SaveDataDto saveDataDto) { // 수정
        this.title = saveDataDto.getTitle();
        this.projectImage = saveDataDto.getProjectImage();
        this.kakaoLink = saveDataDto.getKakaoLink();
        this.info = saveDataDto.getInfo();
        this.freeInfo = saveDataDto.getFreeInfo();
        this.progressMethod = saveDataDto.getProgressMethod();
        this.usedStacks = saveDataDto.getUsedStacks();
        this.referenceLinks = saveDataDto.getReferenceLinks();
        this.isTemporary = saveDataDto.getIsTemporary();

        // 연관관계 매핑 중 콜렉션 데이터 처리
        this.projectHashtags.clear();
        this.projectHashtags.addAll(saveDataDto.getProjectHashtags());
        this.projectPositions.clear();
        this.projectPositions.addAll(saveDataDto.getProjectPositions());

        return this;
    }

    // == 비즈니스 로직 == //
    // 프로젝트 삭제 처리
    public void setDeleteProcessing() {
        this.user = null; // 팀장 삭제
        this.restorationDate = LocalDateTime.now().plusWeeks(2); // 복구 기한 설정
    }

    // 프로젝트 복구
    public void restore(User user) {
        this.user = user; // 복구한 사용자
        this.restorationDate = null;
    }

    // 조회수 증가
    public void increaseViews() {
        this.views += 1;
    }
}
