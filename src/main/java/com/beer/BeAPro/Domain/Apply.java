package com.beer.BeAPro.Domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Apply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 지원자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // 지원 프로젝트

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position; // 지원 포지션

    @Enumerated(EnumType.STRING)
    private JoinStatus joinStatus; // 지원자 합류 여부


    // == 생성 메서드 == //
    @Builder
    public static Apply createApply(User user, Project project, Position position) {
        Apply apply = new Apply();

        apply.user = user;
        apply.project = project;
        apply.position = position;

        apply.joinStatus = JoinStatus.UNCLASSIFIED;

        return apply;
    }
}
