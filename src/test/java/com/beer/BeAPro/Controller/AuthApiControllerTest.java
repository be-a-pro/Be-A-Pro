package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Exception.GlobalExceptionHandler;
import com.beer.BeAPro.Service.AuthService;
import com.beer.BeAPro.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthApiController.class)
class AuthApiControllerTest {

    @MockBean
    AuthService authService;
    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private final String authControllerUrl = "/api/auth";

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthApiController(authService, userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JavaTimeModule())
                .build();
    }


    @DisplayName("약관 동의")
    @Test
    @Rollback(value = false)
    public void setUserTermsAgree() throws Exception {
        // given
        User user = User.registerUserTestOnly("user@email.com");
        String requestAccessTokenInHeader = "Bearer accessToken";
        AuthDto.AgreeDto agreeDto = AuthDto.AgreeDto.builder()
                .provideToThirdParties(true)
                .marketingEmail(true)
                .marketingSMS(true)
                .build();

        Mockito.when(authService.resolveToken(requestAccessTokenInHeader))
                .thenReturn("accessToken");
        Mockito.when(authService.getPrincipal("accessToken"))
                .thenReturn("user@email.com");
        Mockito.when(userService.findByEmail("user@email.com"))
                .thenReturn(user);

        String requestAgreeDto = objectMapper.writeValueAsString(agreeDto);

        // when
        mockMvc.perform(
                post(authControllerUrl + "/agree")
                        .header("Authorization", requestAccessTokenInHeader)
                        .content(requestAgreeDto)
                        .contentType(MediaType.APPLICATION_JSON))
        // then
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("AT 재발급 필요X")
    @Test
    public void validate_success() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer onlyExpirationExceededAccessToken";

        Mockito.when(authService.isRequiredReissue(requestAccessTokenInHeader))
                .thenReturn(false);

        // when
        mockMvc.perform(
                post(authControllerUrl + "/validate")
                        .header("Authorization", requestAccessTokenInHeader)
                        .contentType(MediaType.APPLICATION_JSON))
        // then
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("AT 재발급 필요O: AT가 만료일자만 초과한 유효한 토큰일 경우")
    @Test
    public void isRequiredReissue() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer onlyExpirationExceededAccessToken";

        Mockito.when(authService.isRequiredReissue(requestAccessTokenInHeader))
                .thenReturn(true);

        // when
        mockMvc.perform(
                        post(authControllerUrl + "/validate")
                                .header("Authorization", requestAccessTokenInHeader)
                                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @DisplayName("토큰 재발급(성공)")
    @Test
    public void reissue_success() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer accessToken";
        String requestRefreshToken = "refreshToken";

        Cookie cookie = new Cookie("refresh-token", requestRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(30);
        cookie.setPath("/");

        AuthDto.TokenDto tokenDto = new AuthDto.TokenDto("reissuedAccessToken", "reissuedRefreshToken");

        Mockito.when(authService.reissue(requestAccessTokenInHeader, requestRefreshToken))
                        .thenReturn(tokenDto);

        // when
        mockMvc.perform(
                        post(authControllerUrl + "/reissue")
                                .header("Authorization", requestAccessTokenInHeader)
                                .cookie(cookie)
                                .contentType(MediaType.APPLICATION_JSON)
                )

        // then
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("토큰 재발급(실패)")
    @Test
    public void reissue_fail() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer invalidAccessToken";
        String requestRefreshToken = "invalidRefreshToken";

        Cookie cookie = new Cookie("refresh-token", requestRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(30);
        cookie.setPath("/");

        Mockito.when(authService.reissue(requestAccessTokenInHeader, requestRefreshToken))
                .thenReturn(null);

        // when
        mockMvc.perform(
                        post(authControllerUrl + "/reissue")
                                .header("Authorization", requestAccessTokenInHeader)
                                .cookie(cookie)
                                .contentType(MediaType.APPLICATION_JSON)
                )

                // then
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @DisplayName("로그아웃")
    @Test
    public void logout() throws Exception {
        // given
        String requestAccessTokenInHeader = "Bearer accessToken";

        Mockito.when(authService.logout(requestAccessTokenInHeader)).thenReturn(true);

        // when
        mockMvc.perform(
                post(authControllerUrl + "/logout")
                        .header("Authorization", requestAccessTokenInHeader)
                        .contentType(MediaType.APPLICATION_JSON)
        )

        // then
                .andExpect(status().isOk())
                .andExpect(header().string("refresh-token", (String) null))
                .andDo(print());

    }
}