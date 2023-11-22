package com.likelion.remini.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Type {
    KPT("KPT 회고"),
    CSS("Continue-Stop-Start 회고"),
    FIVE_F("5F 회고"),
    TIL("TIL 회고"),
    FOUR_L("4L 회고"),
    ORID("ORID 회고"),
    AAR("AAR 회고"),
    YWT("YWT 회고"),
    PERSONAL("개인적 회고"),
    RESULT("성과/수치 중심 회고"),
    ;

    private final String name;
}
