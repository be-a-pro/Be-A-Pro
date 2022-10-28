package com.beer.BeAPro.Service;

import com.beer.BeAPro.Domain.OAuthType;
import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Dto.OAuth2NaverUserDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void registerUser(AuthDto.SignupDto signupDto) {
        User user = User.registerUser(signupDto);
        userRepository.save(user);
    }

    @Transactional
    public void registerUserByNaver(OAuth2NaverUserDto oAuth2NaverUserDto) {
        User user = User.registerUserByNaver(oAuth2NaverUserDto);
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
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

    // 약관 동의 여부 값 설정
    @Transactional
    public void setTermsAgree(User user, AuthDto.AgreeDto agreeDto) {
        user.setTermsAgree(agreeDto);
    }
}
