package com.likelion.remini.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReminiException extends RuntimeException implements CustomException {

    private final ErrorResult errorResult;
}
