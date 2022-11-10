package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Domain.OAuthType;
import com.beer.BeAPro.Domain.Role;
import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Dto.OAuth2Attribute;
import com.beer.BeAPro.Dto.OAuth2NaverUserDto;
import com.beer.BeAPro.Exception.ErrorCode;
import com.beer.BeAPro.Exception.RestApiException;
import com.beer.BeAPro.Service.AuthService;
import com.beer.BeAPro.Service.RedisService;
import com.beer.BeAPro.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.nio.charset.StandardCharsets;


@Slf4j
@RestController
@RequestMapping("/api/oauth2/naver")
public class NaverLoginApiController {

    private final String clientId;
    private final String clientSecret;
    private final String clientName;

    private final String redirectUri;
    private final String authorizationUri;
    private final String tokenUri;
    private final String userInfoUri;

    private final AuthService authService;
    private final UserService userService;
    private final RedisService redisService;

    private final long COOKIE_EXPIRATION = 7776000;
    private final String SERVER = "Server";

    public NaverLoginApiController(@Value("${spring.security.oauth2.client.registration.naver.client-id}") String clientId,
                                   @Value("${spring.security.oauth2.client.registration.naver.client-secret}") String clientSecret,
                                   @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}") String redirectUri,
                                   @Value("${spring.security.oauth2.client.registration.naver.client-name}") String clientName,
                                   @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}") String authorizationUri,
                                   @Value("${spring.security.oauth2.client.provider.naver.token-uri}") String tokenUri,
                                   @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}") String userInfoUri,
                                   AuthService authService,
                                   UserService userService,
                                   RedisService redisService) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.clientName = clientName;
        this.authorizationUri = authorizationUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.authService = authService;
        this.userService = userService;
        this.redisService = redisService;
    }

    @RequestMapping(
            value = "/login",
            method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> login() {
        // State Token 생성 및 저장
        String encodedState = encodeUrl(authService.generateState());
        authService.saveState(encodedState);

        // Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // 호출할 API
        String redirectUrl = authorizationUri +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + encodeUrl(redirectUri) +
                "&state=" + encodedState;

        return ResponseEntity.status(HttpStatus.FOUND)
                .headers(headers)
                .location(URI.create(redirectUrl))
                .build();
    }

    // URL 인코딩
    public String encodeUrl(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    // 요청 결과로부터 데이터 추출
    public JsonNode readInfo(String content) {
        try {
            return new ObjectMapper().readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: {}", content);
            throw new RuntimeException("JsonProcessingException");
        }
    }

    // Callback 리다이렉트
    // 토큰 발급 및 저장 후, 사용자 프로필 요청
    // 회원가입/로그인
    @RequestMapping(
            value = "/redirect",
            method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> redirect(@RequestParam(value = "code", required = false) String code,
                                           @RequestParam(value = "state") String state,
                                           @RequestParam(value = "error", required = false) String error,
                                           @RequestParam(value = "error_description", required = false) String errorDescription) {
        // 에러 핸들링
        if (code == null && error == null) {
            log.error("Error: Naver OAuth2 login redirect. (Parameter required.)");
            throw new RestApiException(ErrorCode.PARAMETER_REQUIRED);
        } else if (error != null) {
            log.error("Error: Naver OAuth2 login redirect. ({}: {})", error, errorDescription);
            throw new RestApiException(ErrorCode.BAD_REQUEST);
        }

        // state 검증
        if (!authService.existsState(state)) {
            log.error("Error: Naver OAuth2 login redirect. (Invalid state token.)");
            throw new RestApiException(ErrorCode.BAD_REQUEST);
        }


        // ===== 토큰 발급 API 호출 ===== //

        // Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code"); // 토큰 발급
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("state", state);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                tokenUri,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 요청 결과: 토큰
        JsonNode tokenInfo = readInfo(response.getBody());
        String naverAccessToken = null;
        String naverRefreshToken = null;
        // 에러 발생할 경우
        try {
            if(!tokenInfo.get("error").asText().isEmpty()) {
                return ifError(tokenInfo);
            }
        } catch (NullPointerException e) {
            naverAccessToken = tokenInfo.get("access_token").asText();
            naverRefreshToken = tokenInfo.get("refresh_token").asText();
        }


        // ===== 사용자 네이버 프로필 API 호출 ===== //

        // HTTP Header 값 추가
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + naverAccessToken);

        // HTTP 요청 보내기
        requestEntity = new HttpEntity<>(headers);
        response = new RestTemplate().exchange(
                userInfoUri,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 요청 결과: 네이버 프로필 정보
        JsonNode userInfo = readInfo(response.getBody()).get("response");
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(clientName, userInfo);
        OAuth2NaverUserDto oAuth2NaverUserDto = new OAuth2NaverUserDto(oAuth2Attribute);


        // 사용자 생성/수정
        User findUser = userService.findByEmail(oAuth2Attribute.getEmail());
        if (findUser != null) { // 이미 가입되어 있는 기존 사용자일 경우
            if (findUser.getNaverId() == null) { // 네이버 연동X
                userService.connectNaver(findUser, oAuth2Attribute.getNaverId());
            }
        } else { // 새로운 사용자 -> 회원가입 처리
            findUser = userService.registerUserByNaver(oAuth2NaverUserDto);
        }

        // 사용자 로그인
        userService.login(findUser);
        // Redis에 네이버의 RT 저장
        authService.saveRefreshToken(clientName, oAuth2Attribute.getEmail(), naverRefreshToken);
        // 사용할 토큰 생성 및 저장
        AuthDto.TokenDto tokenDto = authService.generateToken(SERVER, oAuth2Attribute.getEmail(), Role.USER.toString());


        // ===== 응답 ===== //
        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", tokenDto.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken())
                .build();
    }

    // AT 재발급 후, 네이버 로그인 연동 해제(AT 삭제)
    @RequestMapping(
            value = "/disconnect",
            method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> disconnect(@RequestHeader("Authorization") String beaproAccessToken) { // 서버 AT
        // Redis에서 네이버 RT 읽어오기
        String principal = authService.getPrincipal(authService.resolveToken(beaproAccessToken));
        String naverRefreshToken = redisService.getValues("RT(" + clientName + "):" + principal);
        if (naverRefreshToken == null) { // 이미 연동이 해제된 경우
            log.error("Error: Naver OAuth2 disconnect: Not found naver refresh token.");
            throw new RestApiException(ErrorCode.USER_NOT_FOUND);
        }

        // OAuthType 검사
        User findUser = userService.findByEmail(principal);
        if (findUser == null) {
            log.error("Error: Naver OAuth2 disconnect: Not found user.");
            throw new RestApiException(ErrorCode.USER_NOT_FOUND);
        }
        userService.checkOAuthType(findUser, OAuthType.NAVER);


        // ===== 네이버 AT 재발급 API 호출 ===== //

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token"); // 토큰 재발급
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", naverRefreshToken);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                tokenUri,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 요청 결과: AT
        JsonNode responseInfo = readInfo(response.getBody());
        String newNaverAccessToken = null;
        try {
            if(!responseInfo.get("error").asText().isEmpty()) { // 에러 발생할 경우
                return ifError(responseInfo);
            }
        } catch (NullPointerException e) { // 에러가 발생하지 않을 경우
            newNaverAccessToken = responseInfo.get("access_token").asText();
        }


        // ===== 네이버 로그인 연동 해제 API 호출 ===== //

        // HTTP Body 생성
        body = new LinkedMultiValueMap<>();
        body.add("grant_type", "delete"); // 토큰 삭제
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("access_token", encodeUrl(newNaverAccessToken));
        body.add("service_provider", clientName);

        // HTTP 요청 보내기
        requestEntity = new HttpEntity<>(body, headers);
        response = new RestTemplate().exchange(
                tokenUri,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // 요청 결과
        JsonNode deleteInfo = readInfo(response.getBody());
        try {
            if (deleteInfo.get("result").asText().equals("success")) {
                // 네이버 연동 해제
                userService.disconnectNaver(findUser);
            }
        } catch (RuntimeException e) { // 에러 발생할 경우
            throw new RuntimeException("Error: Naver OAuth2 disconnect: Not defined.");
        }

        // RT 삭제
        redisService.deleteValues("RT(" + clientName + "):" + principal); // 네이버
        boolean isLoggedOut = authService.logout(beaproAccessToken);// 비어프로
        if (!isLoggedOut) {
            throw new RestApiException(ErrorCode.LOGOUT_FAILED);
        }

        ResponseCookie responseCookie = ResponseCookie.from("refresh-token", "")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .build();
    }

    // 에러 처리
    public ResponseEntity<String> ifError(JsonNode responseInfo) {
        String error = responseInfo.get("error").asText();
        String errorDescription = responseInfo.get("error_description").asText();
        log.error("Error: {}, Error description: {}", error, errorDescription);

        if (error.equals("invalid_request")) {
            throw new RestApiException(ErrorCode.BAD_REQUEST);
        } else if (error.equals("access_denied")) {
            throw new RestApiException(ErrorCode.ACCESS_DENIED);
        } else {
            throw new RuntimeException("Error not defined.");
        }
    }
}
