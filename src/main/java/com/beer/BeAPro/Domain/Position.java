package com.beer.BeAPro.Domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Position {

    @Id
    @GeneratedValue
    @Column(name = "position_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category; // 분야 [DEVELOPMENT, DESIGN, PLANNING, ETC]

    @Enumerated(EnumType.STRING)
    private Development development;

    @Enumerated(EnumType.STRING)
    private Design design;

    @Enumerated(EnumType.STRING)
    private Planning planning;

    @Enumerated(EnumType.STRING)
    private Etc etc;

}
