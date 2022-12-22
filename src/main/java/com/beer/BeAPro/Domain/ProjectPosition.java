package com.beer.BeAPro.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_position_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", unique = true)
    private Position position;

    private Long currentCount; // 현재 인원

    private Long closingCount; // 마감 인원
    
    private Boolean isClosing; // 포지션별 구인 마감


    // == 생성 메서드 == //
    public static ProjectPosition createProjectPosition(Project project, Position position, Long closingCount) {
        ProjectPosition projectPosition = new ProjectPosition();

        projectPosition.project = project;
        projectPosition.position = position;
        projectPosition.currentCount = 0L;
        projectPosition.closingCount = closingCount;
        projectPosition.isClosing = false;

        return projectPosition;
    }

    // 구인중인 프로젝트에서 팀원 퇴출 또는 탈퇴
    public void withdrawalDuringRecruiting() {
        this.currentCount -= 1;
        if (this.isClosing) {
            this.isClosing = false;
        }
    }

    // 프로젝트 모집 상태 변경
    public void updateClosingCount(Long closingCount) {
        this.closingCount = closingCount;
        this.isClosing = this.currentCount >= closingCount;
    }
}
