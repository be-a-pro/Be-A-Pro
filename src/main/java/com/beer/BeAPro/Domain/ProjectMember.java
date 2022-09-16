package com.beer.BeAPro.Domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ProjectMember {

    @Id @GeneratedValue
    @Column(name = "project_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 팀원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // 프로젝트

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    @Column(unique = true) // FK
    private Position position; // 담당 포지션

}
