package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final AuthService authService;


    // AT 재발급이 필요한지 검사
    @Operation(summary = "Access Token 재발급 필요 여부 검사",
            description = "Access Token이 필요한 요청을 할 경우, 해당 API 먼저 요청할 것")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다음 요청 진행(재발급 필요X)"),
            @ApiResponse(responseCode = "401", description = "요청 이전에 POST /api/auth/reissue 요청을 먼저 진행해 Access Token 재발급 필요"),
            @ApiResponse(responseCode = "500", description = "서버 에러. 버그 리포트 바람.")
    })
    @PostMapping("/validate")
    public ResponseEntity<?> validate(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader) {
        if (!authService.isRequiredReissue(requestAccessTokenInHeader)) {
            return ResponseEntity.status(HttpStatus.OK).build(); // 재발급 필요X
        } else {
            throw new RestApiException(ErrorCode.EXPIRED_TOKEN); // 재발급 필요
        }
    }

    // 토큰 재발급
    @Operation(summary = "Access Token, Refresh Token 재발급",
            description = "Refresh Token과 만료된 Access Token을 함께 보내 토큰을 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 완료. 다음 요청 또는 무시."),
            @ApiResponse(responseCode = "401", description = "Refresh Token 쿠키 탈취 가능성으로 삭제. 재로그인 필요."),
            @ApiResponse(responseCode = "500", description = "서버 에러. 버그 리포트 바람.")
    })
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(
            @Parameter(description = "refresh-token 쿠키") @CookieValue(name = "refresh-token") String requestRefreshToken,
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader) {
        AuthDto.TokenDto reissuedTokenDto = authService.reissue(requestAccessTokenInHeader, requestRefreshToken);

        if (reissuedTokenDto != null) { // 토큰 재발급 성공
            // RT 저장
            ResponseCookie responseCookie = ResponseCookie.from("refresh-token", reissuedTokenDto.getRefreshToken())
                    .maxAge(7776000)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .build();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    // AT 저장
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuedTokenDto.getAccessToken())
                    .build();

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

    // 로그아웃
    @Operation(summary = "로그아웃 및 Refresh Token 쿠키 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refresh Token 삭제 및 로그아웃 성공"),
            @ApiResponse(responseCode = "409", description = "이미 로그아웃된 사용자일 경우"),
            @ApiResponse(responseCode = "500", description = "서버 에러. 버그 리포트 바람.")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Parameter(description = "Access Token", example = "Bearer {access-token}")
            @RequestHeader("Authorization") String requestAccessTokenInHeader) {
        authService.logout(requestAccessTokenInHeader);

        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }
}
