package com.beer.BeAPro.Domain;

import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Dto.OAuth2NaverUserDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String password;

    private String mobile; // 휴대폰 번호 [00000000000]

    private Boolean mobileIsPublic; // 휴대폰 번호 공개 여부

    private String email; // 이메일

    private String birthday; // 생년월일 [yyyyMMdd]

    @OneToMany(mappedBy = "user") // *삭제시 userPositions 엔티티에서도 삭제
    private List<UserPosition> userPositions = new ArrayList<>(); // 사용자 포지션 // 2개 이하

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserHashtag> userHashtags = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_image_id", unique = true)
    private ProfileImage profileImage; // 프로필 이미지

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "portfolio_file_id", unique = true)
    private PortfolioFile portfolioFile; // 포트폴리오 파일

    private String portfolioLinks; // 포트폴리오 링크 // 3개 이하, 구분자 사용 "link1,link2,link3"

    private Boolean portfolioIsPublic; // 포트폴리오 공개 여부

    // private String loginAPI; // 사용 로그인 API

    @Enumerated(EnumType.STRING)
    private Role role; // 사용자 권한

    private Boolean provideToThirdParties; // 제3자 제공 동의 여부

    private Boolean marketingEmail; // 마케팅 수신 동의 여부(이메일)

    private Boolean marketingSMS; // 마케팅 수신 동의 여부(문자)

    private LocalDateTime lastLoginDate; // 마지막 로그인 날짜

    private Boolean isInactive; // 휴면 계정 여부

    private LocalDateTime toInactiveDate; // 휴면 계정 변환 예정 날짜
    
    private LocalDateTime pwModifiedDate; // 비밀번호 변경 날짜


    // ===== OAuth2.0 ===== //
    @Enumerated(EnumType.STRING)
    private OAuthType oAuthType; // 회원 가입할 때 사용한 인증 서버

    private String naverId;
    private LocalDateTime naverConnectedDate; // 네이버 연동 날짜
    private LocalDateTime naverDisconnectedDate; // 네이버 연동 해제 날짜


    // == 생성 메서드 == //
    public static User registerUser(AuthDto.SignupDto signupDto) {
        User user = new User();

        user.email = signupDto.getEmail();
        user.password = signupDto.getPassword();
        user.role = Role.USER;

        return user;
    }

    public static User registerUserByNaver(OAuth2NaverUserDto oAuth2NaverUserDto) {
        User user = new User();

        user.naverId = oAuth2NaverUserDto.getNaverId();
        user.email = oAuth2NaverUserDto.getEmail();
        user.mobile = oAuth2NaverUserDto.getMobile();
        user.name = oAuth2NaverUserDto.getName();
        user.birthday = oAuth2NaverUserDto.getBirthday();
        user.role = Role.USER;
        user.oAuthType = OAuthType.NAVER;

        return user;
    }

    public void connectNaver(String naverId) {
        this.naverId = naverId;
        this.naverConnectedDate = LocalDateTime.now();
    }

    public void disconnectNaver() {
        this.naverId = null;
        this.naverDisconnectedDate = LocalDateTime.now();
    }

    
    // == 비즈니스 로직 == //

    // 약관 동의 여부 값 설정
    public void setTermsAgree(AuthDto.AgreeDto agreeDto) {
        this.provideToThirdParties = agreeDto.getProvideToThirdParties();
        this.marketingEmail = agreeDto.getMarketingEmail();
        this.marketingSMS = agreeDto.getMarketingSMS();
    }
}
