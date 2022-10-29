package com.beer.BeAPro.Domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Design {
    GRAPHIC("그래픽 디자인"),
    THREE_DIMENSIONAL("3D 디자인"),
    CONTENT("컨텐츠 디자인"),
    UXUI("UXUI 디자인"),
    VIDEO("영상 디자인");

    private final String title;
}
