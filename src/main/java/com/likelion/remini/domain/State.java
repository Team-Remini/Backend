package com.likelion.remini.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum State {
    STANDARD("Standard"),
    PREMIUM("Premium"),
    ;

    private final String name;
}
