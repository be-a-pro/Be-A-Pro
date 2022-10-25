package com.beer.BeAPro.Dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2NaverUserDto {

    private String email;
    private String name;
    private String mobile;
    private String birthday;

    private String naverId;

    @Builder
    public OAuth2NaverUserDto(OAuth2Attribute oAuth2Attribute) {
        this.naverId = oAuth2Attribute.getNaverId();
        this.email = oAuth2Attribute.getEmail();
        this.mobile = oAuth2Attribute.getMobile();
        this.name = oAuth2Attribute.getName();
        this.birthday = oAuth2Attribute.getBirthday();
    }
}
