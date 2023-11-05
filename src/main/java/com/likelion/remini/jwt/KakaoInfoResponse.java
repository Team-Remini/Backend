package com.likelion.remini.jwt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoInfoResponse implements OAuthInfoResponse{
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoAccount {
//        private KakaoProfile profile;
        private String email;
    }

//    @Getter
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    static class KakaoProfile {
//        private String nickname;
//        private String profile_image_url;
//    }

    @Override
    public String getEmail() {
        return kakaoAccount.email;
    }

//    @Override
//    public String getNickname() {return kakaoAccount.profile.nickname;}
//
//    @Override
//    public String getProfileImage() {return kakaoAccount.profile.profile_image_url;}

    @Override
    public OAuthProvider getOAuthProvider() {
        return OAuthProvider.KAKAO;
    }
}
