package com.beer.BeAPro.Domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Etc {
    MARKETING("마케팅"),
    FINANCE_ACCOUNTING("재무/회계"),
    SALES("영업"),
    ETC("기타");

    private final String title;
}
