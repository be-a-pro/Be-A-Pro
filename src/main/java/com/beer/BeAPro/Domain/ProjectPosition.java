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
    public static ProjectPosition createProjectPosition(Project project, Position position, Long currentCount, Long closingCount) {
        ProjectPosition projectPosition = new ProjectPosition();

        projectPosition.project = project;
        projectPosition.position = position;
        projectPosition.currentCount = currentCount;
        projectPosition.closingCount = closingCount;
        projectPosition.isClosing = currentCount >= closingCount;

        return projectPosition;
    }
}
