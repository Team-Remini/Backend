package com.likelion.remini.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReminiException extends RuntimeException {

    private final ErrorResult errorResult;
}
