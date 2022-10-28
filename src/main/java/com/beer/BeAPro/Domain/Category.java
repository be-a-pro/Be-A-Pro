package com.beer.BeAPro.Domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Category {
    DEVELOPMENT("개발"),
    DESIGN("디자인"),
    PLANNING("기획"),
    ETC("기타");

    private final String title;
}
