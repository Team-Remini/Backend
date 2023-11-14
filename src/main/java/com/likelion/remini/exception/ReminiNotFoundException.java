package com.likelion.remini.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReminiNotFoundException extends RuntimeException {
    public ReminiNotFoundException(String message) {
        super(message);
    }
}
