package com.likelion.remini.jwt;

import org.springframework.util.MultiValueMap;

//OAuth 요청을 위한 파라미터 값들을 갖고 있는 인터페이스
public interface OAuthLoginParams {
    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}
