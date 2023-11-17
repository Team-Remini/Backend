package com.likelion.remini.exception;

/**
 * 사용자 정의 예외 클래스는 반드시 해당 인터페이스를 구현해야 한다.
 */
public interface CustomException {
    ErrorResult getErrorResult();
}
