package com.beer.BeAPro.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInterestKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_interest_keyword_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String name;


    // == 생성 메서드 == //
    public static UserInterestKeyword createUserInterestKeyword(User user, String name) {
        UserInterestKeyword userInterestKeyword = new UserInterestKeyword();

        userInterestKeyword.user = user;
        userInterestKeyword.name = name;

        return userInterestKeyword;
    }
}
