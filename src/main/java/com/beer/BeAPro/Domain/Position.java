package com.beer.BeAPro.Domain;

import com.beer.BeAPro.Dto.RequestDto;
import lombok.AccessLevel;
import lombok.Builder;
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


    // == 생성 메서드 == //
    @Builder
    public static Position createPosition(RequestDto.PositionDto positionDto) {
        Position position = new Position();

        position.category = positionDto.getCategory();
        position.design = positionDto.getDesign();
        position.development = positionDto.getDevelopment();
        position.planning = positionDto.getPlanning();
        position.etc = positionDto.getEtc();

        return position;
    }
}
