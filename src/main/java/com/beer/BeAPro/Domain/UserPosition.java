package com.beer.BeAPro.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_position_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 사용자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", unique = true)
    private Position position; // 사용자의 포지션

    @Enumerated(EnumType.STRING)
    private Career career; // 해당 포지션 경력
}
