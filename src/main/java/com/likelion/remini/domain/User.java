package com.likelion.remini.domain;

import com.likelion.remini.jwt.OAuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String profileImageURL;

    private String email;

    private String state;

    private Date expirationDate;

    private OAuthProvider oAuthProvider;

    @Builder
    public User(String email, String nickname, String profileImageURL,String state, Date expirationDate, OAuthProvider oAuthProvider) {
        this.nickname = nickname;
        this.profileImageURL = profileImageURL;
        this.email = email;
        this.state = state;
        this.expirationDate = expirationDate;
        this.oAuthProvider = oAuthProvider;
    }

//    @Builder
//    public User(String email, OAuthProvider oAuthProvider) {
//        this.email = email;
//        this.oAuthProvider = oAuthProvider;
//    }
}
