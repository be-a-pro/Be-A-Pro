package com.beer.BeAPro.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
