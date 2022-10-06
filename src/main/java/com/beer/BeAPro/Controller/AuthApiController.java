package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Service.AuthService;
import com.beer.BeAPro.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid AuthDto.SignupDto signupDto) {
        Long userId = userService.registerUser(signupDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 로그인 -> 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDto.LoginDto loginDto) {
        // User 등록 및 Refresh Token 저장
        AuthDto.TokenDto tokenDto = authService.login(loginDto);
        userService.saveRefreshToken(loginDto.getEmail(), tokenDto.getRefreshToken());

        // Refresh Token을 cookie에 저장
        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(tokenDto.getAccessToken());
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@CookieValue(name = "refresh-token") String requestRefreshToken,
            @RequestBody String requestAccessToken) {

        AuthDto.TokenDto reissuedTokenDto =
                authService.reissue(requestAccessToken, requestRefreshToken);

        if (reissuedTokenDto != null) { // 토큰 재발급 성공
            // Cookie에 Refresh Token 세팅
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                    .httpOnly(true)
                    .secure(true)
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(reissuedTokenDto.getAccessToken());
        } else { // Refresh Token 탈취 가능성
            // Cookie 삭제 후 재로그인 유도
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                    .maxAge(0)
                    .path("/")
                    .build();
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .build();
        }
    }

}
