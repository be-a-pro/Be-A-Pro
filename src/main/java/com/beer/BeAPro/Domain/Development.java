package com.beer.BeAPro.Domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Development {
    IOS("iOS"),
    CROSS_PLATFORM("크로스플랫폼"),
    WEB_SERVERS("웹 서버"),
    AI("AI"),
    ANDROID("안드로이드"),
    BLOCKCHAIN("블록체인"),
    WEB_PUBLISHER("웹 퍼블리셔"),
    DB("DB");

    private final String title;
}
