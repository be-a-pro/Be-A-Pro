package com.beer.BeAPro.Security;

import com.beer.BeAPro.Service.RedisService;
import io.jsonwebtoken.IncorrectClaimException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Access Token 추출
        String accessToken = resolveToken(request);

        try { // 정상 토큰인지 검사
            if (accessToken != null && jwtTokenProvider.validateTokenOnlyExpired(accessToken)) {
                // Redis에서 logout 여부 확인
                String isLogout = redisService.getValues(accessToken);
                if (isLogout == null) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Save authentication in SecurityContextHolder.");
                } else { // 로그아웃된 AT 사용
                    log.debug("Logout token: " + accessToken);
                    response.sendError(401);
                }
            }
        } catch (IncorrectClaimException e) { // 잘못된 토큰일 경우
            SecurityContextHolder.clearContext();
            log.debug("Invalid JWT token.");
            response.sendError(403);
        } catch (UsernameNotFoundException e) { // 회원을 찾을 수 없을 경우
            SecurityContextHolder.clearContext();
            log.debug("Can't find user.");
            response.sendError(403);
        }

        filterChain.doFilter(request, response);
    }

    // HTTP Request 헤더로부터 토큰 추출
    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}