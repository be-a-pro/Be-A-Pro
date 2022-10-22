package com.beer.BeAPro.Controller;

import com.beer.BeAPro.BaseTests;
import com.beer.BeAPro.Dto.AuthDto;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class AuthApiControllerTest extends BaseTests {

    public final String authControllerUrl = "/api/auth";

    @BeforeEach
    public void beforeEach() {
        objectMapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JavaTimeModule())
                .build();
    }
    @Test
    @Transactional
    public void 회원가입_로그인_재발급_로그아웃() throws Exception {
        // given
        AuthDto.SignupDto signupDto = AuthDto.SignupDto.builder()
                .email("beer@gmail.com")
                .password("123")
                .build();

        AuthDto.LoginDto loginDto = AuthDto.LoginDto.builder()
                .email("beer@gmail.com")
                .password("123")
                .build();

        // when
        String signupContent = objectMapper.writeValueAsString(signupDto);
        String loginContent = objectMapper.writeValueAsString(loginDto);

        // then
        // 회원가입
        mockMvc.perform(post(authControllerUrl+"/signup")
                        .content(signupContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        // 로그인
        ResultActions resultActions = mockMvc.perform(post(authControllerUrl + "/login")
                        .content(loginContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        // 토큰 추출
        List<Cookie> cookies = Arrays.stream(resultActions.andReturn().getResponse().getCookies())
                .filter(c -> c.getName().equals("refresh-token"))
                .collect(Collectors.toList());
        if (cookies.isEmpty()) {
            throw new NullPointerException("Refresh token is empty.");
        }
        String refreshToken = cookies.get(0).getValue();
        String authorizationHeader = resultActions.andReturn().getResponse().getHeader("Authorization");
        String accessToken = resolveToken(authorizationHeader);

        // 쿠키 설정
        Cookie cookie = new Cookie("refresh-token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(60);

        // 재발급
        mockMvc.perform(post(authControllerUrl+"/reissue")
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401))
                .andDo(print());

        // 로그아웃
        mockMvc.perform(post(authControllerUrl+"/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andDo(print());
    }

    // Access Token 추출
    public String resolveToken(String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        return null;
    }
}