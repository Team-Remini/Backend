package com.likelion.remini.jwt;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    String getProfileImageURL();
    OAuthProvider getOAuthProvider();
}
