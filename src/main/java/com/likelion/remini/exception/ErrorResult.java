package com.likelion.remini.exception;

import org.springframework.http.HttpStatus;

public interface ErrorResult {

    HttpStatus getHttpStatus();

    String getMessage();

    String name();
}
