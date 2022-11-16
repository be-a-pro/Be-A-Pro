package com.beer.BeAPro.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 팀원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // 프로젝트

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", unique = true)
    private Position position; // 담당 포지션

    @Enumerated(EnumType.STRING)
    private TeamPosition teamPosition;

    // == 생성 메서드 == //
    public static ProjectMember createProjectMember(User user, Project project, Position position, TeamPosition teamPosition) {
        ProjectMember projectMember = new ProjectMember();

        projectMember.user = user;
        projectMember.project = project;
        projectMember.position = position;
        projectMember.teamPosition = teamPosition;

        return projectMember;
    }
}
