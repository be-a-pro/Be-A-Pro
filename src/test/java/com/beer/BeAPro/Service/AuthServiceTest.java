package com.beer.BeAPro.Service;

import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Security.JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    RedisService redisService;
    @Spy
    @InjectMocks
    AuthService authService;

    String SERVER = "Server";


    @DisplayName("AT가 만료일자만 초과 & 유효한 토큰인지 검사")
    @Test
    public void validate() throws Exception {
        // given
        String validAccessTokenInHeader = "Bearer valid"; // 유효O
        String invalidAccessTokenInHeader = "Bearer invalid"; // 유효X
        String onlyExceededAccessTokenInHeader = "Bearer onlyExpirationExceeded"; // 만료일자 제외하고 유효

        Mockito.when(authService.isRequiredReissue(validAccessTokenInHeader)).thenReturn(false);
        Mockito.when(authService.isRequiredReissue(invalidAccessTokenInHeader)).thenReturn(false);
        Mockito.when(authService.isRequiredReissue(onlyExceededAccessTokenInHeader)).thenReturn(true);

        // when
        boolean validateValidAccessToken = authService.isRequiredReissue(validAccessTokenInHeader);
        boolean validateInvalidAccessToken = authService.isRequiredReissue(invalidAccessTokenInHeader);
        boolean validateOnlyExceededAccessToken = authService.isRequiredReissue(onlyExceededAccessTokenInHeader);

        // then
        assertFalse(validateValidAccessToken);
        assertFalse(validateInvalidAccessToken);
        assertTrue(validateOnlyExceededAccessToken);
    }

    @DisplayName("재발급(성공)")
    @Test
    public void reissue_success() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer accessToken";
        String requestRefreshToken = "refreshToken";

        String email = "user@email.com";
        String authorities = "ROLE_USER";
        AuthDto.TokenDto returnTokenDto
                = new AuthDto.TokenDto("accessToken", "refreshToken");

        Mockito.doReturn(email)
                .when(authService)
                .getPrincipal("accessToken");
        Mockito.when(redisService.getValues("RT(" + SERVER + "):" + email)).thenReturn("refreshToken"); // refreshTokenInRedis
        Mockito.when(jwtTokenProvider.validateRefreshToken(requestRefreshToken)).thenReturn(true);
        Mockito.doReturn(authorities).when(authService).getAuthorities(null); // 테스트용
        Mockito.when(jwtTokenProvider.createToken(email, authorities)).thenReturn(returnTokenDto);
        Mockito.doNothing()
                .when(authService)
                .saveRefreshToken(SERVER, email, returnTokenDto.getRefreshToken());

        Mockito.when(authService.reissue(requestAccessTokenInHeader, requestRefreshToken))
                .thenReturn(returnTokenDto);

        // when
        AuthDto.TokenDto reissuedToken = authService.reissue(requestAccessTokenInHeader, requestRefreshToken);

        // then
        assertEquals(returnTokenDto, reissuedToken);
    }

    @DisplayName("재발급(실패): Redis에 저장되어 있는 RT가 없을 경우")
    @Test
    public void reissue_fail_1() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer accessToken";
        String requestRefreshToken = "refreshToken";

        String email = "user@email.com";

        Mockito.doReturn(email)
                .when(authService)
                .getPrincipal("accessToken");
        Mockito.when(redisService.getValues("RT(" + SERVER + "):" + email))
                .thenReturn(null); // Redis에 저장되어 있는 RT가 없음

        Mockito.when(authService.reissue(requestAccessTokenInHeader, requestRefreshToken))
                .thenReturn(null);

        // when
        AuthDto.TokenDto reissuedToken = authService.reissue(requestAccessTokenInHeader, requestRefreshToken);

        // then
        assertNull(reissuedToken);
    }

    @DisplayName("재발급(실패): RT가 유효하지 않을 경우")
    @Test
    public void reissue_fail_2() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer accessToken";
        String requestRefreshToken = "refreshToken";

        String email = "user@email.com";

        Mockito.doReturn(email)
                .when(authService)
                .getPrincipal("accessToken");
        Mockito.when(redisService.getValues("RT(" + SERVER + "):" + email))
                .thenReturn("refreshToken"); // refreshTokenInRedis
        Mockito.when(jwtTokenProvider.validateRefreshToken(requestRefreshToken))
                .thenReturn(false); // RT 유효X

        // when
        AuthDto.TokenDto reissuedToken = authService.reissue(requestAccessTokenInHeader, requestRefreshToken);

        // then
        assertNull(reissuedToken);
    }

    @DisplayName("재발급(실패): Redis에 저장되어 있는 RT와 다를 경우")
    @Test
    public void reissue_fail_3() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer accessToken";
        String requestRefreshToken = "refreshToken";

        String email = "user@email.com";
        String authorities = "ROLE_USER";
        AuthDto.TokenDto returnTokenDto
                = new AuthDto.TokenDto("accessToken", "refreshToken");

        Mockito.doReturn(email)
                .when(authService)
                .getPrincipal("accessToken");
        Mockito.when(redisService.getValues("RT(" + SERVER + "):" + email))
                .thenReturn("differenceRefreshToken"); // refreshTokenInRedis
        Mockito.when(jwtTokenProvider.validateRefreshToken(requestRefreshToken)).thenReturn(true);

        // when
        AuthDto.TokenDto reissuedToken = authService.reissue(requestAccessTokenInHeader, requestRefreshToken);

        // then
        assertNull(reissuedToken);
    }

    @DisplayName("토큰 발급")
    @Test
    public void generateToken() throws Exception {
        // given
        String provider = SERVER;
        String email = "user@email.com";
        String authorities = "ROLE_USER";

        AuthDto.TokenDto returnTokenDto = new AuthDto.TokenDto("accessToken", "refreshToken");

        Mockito.when(jwtTokenProvider.createToken(email, authorities))
                .thenReturn(returnTokenDto);
        Mockito.doNothing()
                .when(authService)
                .saveRefreshToken(provider, email, returnTokenDto.getRefreshToken());
        Mockito.when(authService.generateToken(provider, email, authorities))
                .thenReturn(returnTokenDto);

        // when
        AuthDto.TokenDto generatedToken = authService.generateToken(provider, email, authorities);

        // then
        assertEquals(returnTokenDto, generatedToken);
    }

    @DisplayName("Redis에 RT 저장")
    @Test
    public void saveRefreshToken() throws Exception {
        // given
        String provider = SERVER;
        String email = "user@email.com";
        String refreshToken = "refreshToken";

        Mockito.when(jwtTokenProvider.getTokenExpirationTime(refreshToken)).thenReturn(10L);

        // when
        authService.saveRefreshToken(provider, email, refreshToken);

        // then
    }

    @DisplayName("AT로부터 principal 추출")
    @Test
    public void getPrincipal() throws Exception {
        // given
        String tokenPrincipal = "user@email.com";
        String requestAccessToken = "accessToken";

        Mockito.doReturn(tokenPrincipal)
                .when(authService)
                .getPrincipal(requestAccessToken);

        // when
        String principal = authService.getPrincipal(requestAccessToken);

        // then
        assertEquals(tokenPrincipal, principal);
    }

    @DisplayName("Authorization Header로부터 AT 추출")
    @Test
    public void resolveToken() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer accessToken";

        Mockito.when(authService.resolveToken(requestAccessTokenInHeader))
                .thenReturn("accessToken");

        // when
        String accessToken = authService.resolveToken(requestAccessTokenInHeader);

        // then
        assertEquals("accessToken", accessToken);
    }

    @DisplayName("로그아웃")
    @Test
    public void logout() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer accessToken";
        String tokenPrincipal = "user@email.com";

        Mockito.doReturn(tokenPrincipal)
                .when(authService)
                .getPrincipal("accessToken");
        Mockito.when(redisService.getValues("RT(" + SERVER + "):" + tokenPrincipal))
                        .thenReturn("refreshToken"); // refreshTokenInRedis
        Mockito.when(jwtTokenProvider.getTokenExpirationTime("accessToken")).thenReturn(new Date().getTime() + 30);

        // when
        String requestAccessToken = authService.resolveToken(requestAccessTokenInHeader);
        String email = authService.getPrincipal(requestAccessToken);
        String refreshTokenInRedis = redisService.getValues("RT(" + SERVER + "):" + email);
        long expiration = jwtTokenProvider.getTokenExpirationTime(requestAccessToken) - new Date().getTime();

        // then
        assertEquals("accessToken", requestAccessToken);
        assertEquals(tokenPrincipal, email);
        assertEquals("refreshToken", refreshTokenInRedis);
        assertTrue(expiration > 0);
    }
}
