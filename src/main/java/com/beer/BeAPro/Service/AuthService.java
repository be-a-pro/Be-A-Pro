package com.beer.BeAPro.Service;

import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;

    // 로그인, 토큰 발급
    @Transactional
    public AuthDto.TokenDto login(AuthDto.LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String authorities = getAuthorities(authentication);

        // RT가 이미 있을 경우
        if(redisService.getValues("RT:" + authentication.getName()) != null) {
            redisService.deleteValues("RT:" + authentication.getName()); // 삭제
        }

        // AT, RT 생성 및 Redis에 RT 저장
        AuthDto.TokenDto tokenDto = jwtTokenProvider.createToken(authentication.getName(), authorities);
        saveRefreshToken(authentication.getName(), tokenDto.getRefreshToken());
        return tokenDto;
    }
    
    // AT가 만료일자만 초과한 유효한 토큰일 때 -> AT, RT 재발급
    @Transactional
    public AuthDto.TokenDto reissue(String requestAccessTokenInHeader, String requestRefreshToken) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        // 만료되지 않은 AT일 경우, 재발급 필요X
        if (!jwtTokenProvider.validateTokenOnlyExpired(requestAccessToken)) {
            throw new AccessDeniedException("Not required.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

        String refreshTokenInRedis = redisService.getValues("RT:" + authentication.getName());
        if (refreshTokenInRedis == null) { // Redis에 저장되어 있는 RT가 없을 경우
            return null; // -> 재로그인 요청
        }

        // 요청된 RT의 유효성 검사 & Redis에 저장되어 있는 RT와 같은지 비교
        if(!jwtTokenProvider.validateToken(requestRefreshToken) || !refreshTokenInRedis.equals(requestRefreshToken)) {
            redisService.deleteValues("RT:" + authentication.getName()); // 탈취 가능성 -> 삭제
            return null; // -> 재로그인 요청
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String authorities = getAuthorities(authentication);

        // 토큰 재발급 및 Redis 업데이트
        redisService.deleteValues("RT:" + authentication.getName()); // 기존 RT 삭제
        AuthDto.TokenDto tokenDto = jwtTokenProvider.createToken(authentication.getName(), authorities);
        saveRefreshToken(authentication.getName(), tokenDto.getRefreshToken());
        return tokenDto;
    }

    // RT를 Redis에 저장
    @Transactional
    public void saveRefreshToken(String principal, String refreshToken) {
        redisService.setValuesWithTimeout("RT:" + principal, // key
                refreshToken, // value
                jwtTokenProvider.getTokenExpirationTime(refreshToken)); // timeout(milliseconds)
    }

    // 권한 이름 가져오기
    public String getAuthorities(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    // "Bearer {AT}"에서 {AT} 추출
    public String resolveToken(String requestAccessToken) {
        if (requestAccessToken != null && requestAccessToken.startsWith("Bearer ")) {
            return requestAccessToken.substring(7);
        }
        return null;
    }

    // 로그아웃
    @Transactional
    public void logout(String requestAccessTokenInHeader) {
        String requestAccessToken = resolveToken(requestAccessTokenInHeader);
        if (!jwtTokenProvider.validateToken(requestAccessToken)) {
            throw new RequestRejectedException("Invalid request.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(requestAccessToken);

        // Redis에 저장되어 있는 RT 삭제
        String refreshTokenInRedis = redisService.getValues("RT:" + authentication.getName());
        if (refreshTokenInRedis != null) {
            redisService.deleteValues("RT:" + authentication.getName());
        }

        // Redis에 로그아웃 처리한 AT 저장
        long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();
        redisService.setValuesWithTimeout(requestAccessToken,
                "logout",
                expiration);
    }

}