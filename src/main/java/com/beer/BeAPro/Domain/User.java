package com.beer.BeAPro.Domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class User extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String mobile; // 휴대폰 번호 [00000000000]

    private Boolean mobileIsPublic; // 휴대폰 번호 공개 여부

    private String email; // 이메일

    private String birthday; // 생년월일 [yyyyMMdd]

    @OneToMany(mappedBy = "user") // *삭제시 userPositions 엔티티에서도 삭제
    private List<UserPosition> userPositions = new ArrayList<>(); // 사용자 포지션 // 2개 이하

    private List<String> hashtags = new ArrayList<>(); // 해시태그

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_image_id")
    @Column(unique = true) // FK
    private ProfileImage profileImage; // 프로필 이미지

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "portfolio_file_id")
    @Column(unique = true) // FK
    private PortfolioFile portfolioFile; // 포트폴리오 파일

    private String portfolioLink; // 포트폴리오 링크

    private Boolean portfolioIsPublic; // 포트폴리오 공개 여부

    // private String loginAPI; // 사용 로그인 API

    @Enumerated(EnumType.STRING)
    private Auth auth; // 사용자 권한

    private Boolean marketingEmail; // 마케팅 수신 동의 여부(이메일)

    private Boolean marketingSMS; // 마케팅 수신 동의 여부(문자)

    private LocalDateTime lastLoginDate; // 마지막 로그인 날짜

    private Boolean isInactive; // 휴면 계정 여부

    private LocalDateTime toInactiveDate; // 휴면 계정 변환 예정 날짜
    
    private LocalDateTime pwModifiedDate; // 비밀번호 변경 날짜

}
