package com.beer.BeAPro.Domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Planning {
    UX("UX 기획"),
    PRODUCT_MANAGER("프로덕트 매니저"),
    SERVICE("서비스 기획"),
    PRODUCT("제품 기획");

    private final String title;
}
