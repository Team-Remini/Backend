package com.likelion.remini.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException implements CustomException {

    private final ErrorResult errorResult;
}
