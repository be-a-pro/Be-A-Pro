package com.beer.BeAPro.Controller;

import com.beer.BeAPro.Domain.User;
import com.beer.BeAPro.Dto.AuthDto;
import com.beer.BeAPro.Exception.GlobalExceptionHandler;
import com.beer.BeAPro.Service.AuthService;
import com.beer.BeAPro.Service.FileUploadService;
import com.beer.BeAPro.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthApiController.class)
public class UserApiControllerTest {

    @MockBean
    FileUploadService fileUploadService;
    @MockBean
    UserService userService;
    @MockBean
    AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private final String userControllerUrl = "/api/user";


    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new UserApiController(fileUploadService, userService, authService))
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
                        post(userControllerUrl + "/agree")
                                .header("Authorization", requestAccessTokenInHeader)
                                .content(requestAgreeDto)
                                .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }
}
