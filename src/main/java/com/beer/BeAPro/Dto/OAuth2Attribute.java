package com.beer.BeAPro.Dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(access = AccessLevel.PRIVATE)
public class OAuth2Attribute {

    private String email;
    private String name;
    private String mobile;
    private String birthday;

    private static final String NAVER = "NAVER";
    private String naverId;


    // 응답으로 받는 데이터를 인증 서버마다 다르게 처리
    public static OAuth2Attribute of(String provider, JsonNode userInfo) {
        switch (provider) {
            case NAVER:
                return ofNaver(userInfo);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Attribute ofNaver(JsonNode userInfo) {
        return OAuth2Attribute.builder()
                .naverId(userInfo.get("id").asText())
                .email(userInfo.get("email").asText())
                .name(userInfo.get("name").asText())
                .mobile(userInfo.get("mobile").asText().replaceAll("-", ""))
                .birthday(userInfo.get("birthyear").asText() +
                        userInfo.get("birthday").asText().replaceAll("-", ""))
                .build();
    }
}