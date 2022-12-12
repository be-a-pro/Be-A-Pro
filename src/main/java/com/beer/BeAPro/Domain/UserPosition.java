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

    private boolean isRepresentative;


    // == 생성 메서드 == //
    public static UserPosition createUserPosition(User user, Position position, boolean isRepresentative) {
        UserPosition userPosition = new UserPosition();

        userPosition.user = user;
        userPosition.position = position;
        userPosition.isRepresentative = isRepresentative;

        return userPosition;
    }
}
