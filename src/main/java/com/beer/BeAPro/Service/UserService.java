package com.beer.BeAPro.Service;

import com.beer.BeAPro.Domain.*;
import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Dto.OAuth2NaverUserDto;
import com.beer.BeAPro.Dto.RequestDto;
import com.beer.BeAPro.Dto.UserDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserInterestKeywordRepository userInterestKeywordRepository;
    private final UserToolRepository userToolRepository;
    private final UserPositionRepository userPositionRepository;
    private final PositionRepository positionRepository;


    // ===== 생성 및 삭제 ===== //
    @Transactional
    public User registerUserByNaver(OAuth2NaverUserDto oAuth2NaverUserDto) {
        User user = User.registerUserByNaver(oAuth2NaverUserDto);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public void connectNaver(User user, String naverId) {
        user.connectNaver(naverId);
    }

    @Transactional
    public void disconnectNaver(User user) {
        user.disconnectNaver();
    }

    // OAuthType 검사
    public void checkOAuthType(User user, OAuthType oAuthType) {
        if (user.getOAuthType() == oAuthType) { // 회원 가입시 사용했을 경우
            throw new RestApiException(ErrorCode.CANNOT_DISCONNECT);
        }
    }


    // ===== 조회 ===== //

    public List<User> findUserToInactive() {
        return userRepository.findAll().stream()
                .filter(user -> user.getIsInactive().equals(false))
                .filter(user -> user.getToInactiveDate().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }


    // ===== 세부 설정 ===== //

    @Transactional
    public void login(User user) { // 로그인
        user.login();
    }

    @Transactional
    public void setInactive(User user) { // 휴면 계정 전환
        user.setInactive();
    }

    @Transactional
    public void setEnable(User user, Boolean bool) { // 비활성화(정지) 여부 결정
        user.setEnable(bool);
    }


    // ===== 비즈니스 로직 ===== //

    // 약관 동의 여부 값 설정
    @Transactional
    public void setTermsAgree(User user, AuthDto.AgreeDto agreeDto) {
        user.setTermsAgree(agreeDto);
    }

    // 추가 정보 저장
    @Transactional
    public void saveUserAdditionalInfo(User user, RequestDto.SignUpAdditionalInfoDto signUpAdditionalInfoDto) {
        // 데이터 가공
        List<Position> positions = signUpAdditionalInfoDto.getUserPositions().stream()
                .map(Position::createPosition) // Position 생성
                .collect(Collectors.toList());
        List<UserPosition> userPositions = positions.stream()
                .map(position -> UserPosition.createUserPosition(user, position)) // UserPosition 생성
                .collect(Collectors.toList());
        List<UserInterestKeyword> userInterestKeywords = signUpAdditionalInfoDto.getUserInterestKeywords().stream()
                .map(keyword -> UserInterestKeyword.createUserInterestKeyword(user, keyword)) // UserInterestKeyword 생성
                .collect(Collectors.toList());
        List<UserTool> userTools = signUpAdditionalInfoDto.getUserTools().stream()
                .map(userTool -> UserTool.createUserTool(user, userTool)) // UserTool 생성
                .collect(Collectors.toList());
        String portfolioLinks = String.join(",", signUpAdditionalInfoDto.getPortfolioLinks());

        // DTO 생성
        UserDto.SignUpAdditionalInfoDto info = UserDto.SignUpAdditionalInfoDto.builder()
                .mobileIsPublic(signUpAdditionalInfoDto.getMobileIsPublic())
                .userPositions(userPositions)
                .userInterestKeywords(userInterestKeywords)
                .userTools(userTools)
                .portfolioIsPublic(signUpAdditionalInfoDto.getPortfolioIsPublic())
                .portfolioLinks(portfolioLinks)
                .build();

        // 저장
        positionRepository.saveAll(positions);
        userPositionRepository.saveAll(userPositions);
        userInterestKeywordRepository.saveAll(userInterestKeywords);
        userToolRepository.saveAll(userTools);
        user.saveUserAdditionalInfo(info);
    }

    // 포트폴리오 파일 업로드
    @Transactional
    public void setPortfolioFile(User user, PortfolioFile portfolioFile) {
        user.setPortfolioFile(portfolioFile);
    }
}
