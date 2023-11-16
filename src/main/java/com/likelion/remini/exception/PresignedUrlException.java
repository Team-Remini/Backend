package com.likelion.remini.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PresignedUrlException extends RuntimeException {

    public PresignedUrlException(String message) {
        super(message);
    }
}
