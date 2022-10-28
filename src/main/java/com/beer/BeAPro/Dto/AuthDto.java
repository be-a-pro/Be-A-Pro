package com.beer.BeAPro.Dto;

import lombok.*;

import javax.validation.constraints.NotBlank;


public class AuthDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginDto {
        private String email;
        private String password;

        @Builder
        public LoginDto(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignupDto {
        private String email;
        private String password;

        @Builder
        public SignupDto(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public static SignupDto encodePassword(SignupDto signupDto, String encodedPassword) {
            SignupDto newSignupDto = new SignupDto();
            newSignupDto.email = signupDto.getEmail();
            newSignupDto.password = encodedPassword;
            return newSignupDto;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AgreeDto {
        @NotBlank
        private Boolean provideToThirdParties; // 제3자 제공 동의 여부
        @NotBlank
        private Boolean marketingEmail; // 마케팅 수신 동의 여부(이메일)
        @NotBlank
        private Boolean marketingSMS; // 마케팅 수신 동의 여부(문자)

        @Builder
        public AgreeDto(Boolean provideToThirdParties, Boolean marketingEmail, Boolean marketingSMS) {
            this.provideToThirdParties = provideToThirdParties;
            this.marketingEmail = marketingEmail;
            this.marketingSMS = marketingSMS;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;

        public TokenDto(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}
