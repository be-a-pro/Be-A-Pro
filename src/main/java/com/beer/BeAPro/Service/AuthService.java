package com.beer.BeAPro.Service;

import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Repository.UserRepository;
import com.beer.BeAPro.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final UserService userService;

    // 로그인, 토큰 발급
    public AuthDto.TokenDto login(AuthDto.LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String authorities = getAuthorities(authentication);

        return jwtTokenProvider.createToken(authentication.getName(), authorities);
    }

    // Access Token이 만료일자만 초과한 유효한 토큰인지 검사 후, 토큰 재발급
    public AuthDto.TokenDto reissue(String requestAccessToken, String requestRefreshToken) {
        // 만료되지 않은 Access Token -> 재발급 필요X
        if (requestAccessToken != null && !jwtTokenProvider.validateTokenOnlyExpired(requestAccessToken)) {
            throw new AccessDeniedException("");
        }

        // AT가 없거나(새로고침), 만료만 된 유효한 AT가 있을경우
        // RT를 이용한 재발급

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

        String userRefreshToken = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> {
                    throw new NullPointerException("Can't find user with this email. -> "
                            + authentication.getName());
                })
                .getRefreshToken();

        // Refresh Token 유효성 검사 & DB에 저장된 토큰과 비교
        if(!jwtTokenProvider.validateToken(requestRefreshToken) || !requestRefreshToken.equals(userRefreshToken)) {
            userService.deleteRefreshToken(authentication.getName()); // DB에서 삭제
            return null; // -> 재로그인 요청
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        return jwtTokenProvider.createToken(authentication.getName(), authorities);
    }

    // 권한 이름 가져오기
    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    // 로그아웃
    public void logout(String requestAccessToken, String requestRefreshToken) {
    }

}