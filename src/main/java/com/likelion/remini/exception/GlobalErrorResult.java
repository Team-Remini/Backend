package com.likelion.remini.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorResult implements ErrorResult {

    PRESIGNED_URL_FAILED(HttpStatus.BAD_REQUEST, "Failed to generate Pre-Signed URL"),

    UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Exception")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
