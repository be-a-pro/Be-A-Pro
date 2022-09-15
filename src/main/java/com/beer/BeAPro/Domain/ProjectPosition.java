package com.beer.BeAPro.Domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ProjectPosition {

    @Id
    @GeneratedValue
    @Column(name = "project_position_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

}
