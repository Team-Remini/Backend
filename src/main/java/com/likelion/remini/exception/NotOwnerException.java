package com.likelion.remini.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotOwnerException extends RuntimeException {

    public NotOwnerException(String message) {
        super(message);
    }
}
