package com.likelion.remini.jwt;

//Authorization Code 를 기반으로 타플랫폼 Access Token 을 받아오기 위한 Response Model

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoTokens {
    /*
    @JsonProperty 주석은 JSON 데이터의 필드와
    Java 객체의 멤버 변수 간의 매핑을 지정하는 데 사용.
     */
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private String expiresIn;

    @JsonProperty("refresh_token_expires_in")
    private String refreshTokenExpiresIn;

    @JsonProperty("scope")
    private String scope;
}
