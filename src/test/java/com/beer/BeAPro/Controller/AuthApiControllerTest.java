package com.beer.BeAPro.Controller;

import com.beer.BeAPro.BaseTests;
import com.beer.BeAPro.Dto.AuthDto;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class AuthApiControllerTest extends BaseTests {

    @BeforeEach
    public void beforeEach() {
        objectMapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JavaTimeModule())
                .build();
    }

    @Test
    @Transactional
    @Rollback()
    public void 회원가입_로그인() throws Exception {
        // given
        String authControllerUrl = "/api/auth";
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
        mockMvc.perform(post(authControllerUrl+"/login")
                        .content(loginContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}