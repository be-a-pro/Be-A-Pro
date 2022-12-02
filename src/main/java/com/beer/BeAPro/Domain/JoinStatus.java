package com.beer.BeAPro.Domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum JoinStatus {
    UNCLASSIFIED("열람 전"),
    CHECKING("열람 중"),
    NOT_JOINED("미참여");

    private final String title;
}
