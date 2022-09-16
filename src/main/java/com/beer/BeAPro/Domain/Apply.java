package com.beer.BeAPro.Domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Apply {

    @Id
    @GeneratedValue
    @Column(name = "position_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 지원자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project; // 지원 프로젝트

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    @Column(unique = true) // FK
    private Position position; // 지원 포지션

    @Enumerated(EnumType.STRING)
    private JoinStatus joinStatus; // 지원자 합류 여부
}
