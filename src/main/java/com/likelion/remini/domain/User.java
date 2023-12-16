package com.likelion.remini.domain;

import com.likelion.remini.jwt.OAuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="user_id")
    private Long id;

    private String nickname;

    private String profileImageURL;

    private String email;

    @Enumerated(EnumType.STRING)
    private State state;

    @Enumerated(EnumType.STRING)
    private State toBeState;

    private LocalDateTime expirationDate;

    private LocalDateTime alarmTime;

    @Enumerated(value = EnumType.STRING)
    private OAuthProvider oAuthProvider;

    @OneToMany(mappedBy = "user")
    private List<Remini> reminiList = new ArrayList<>();

    @Builder
    public User(String email, String nickname, String profileImageURL, State state,State toBeState, LocalDateTime expirationDate, LocalDateTime alarmTime, OAuthProvider oAuthProvider, List<Remini> reminiList) {
        this.nickname = nickname;
        this.profileImageURL = profileImageURL;
        this.email = email;
        this.state = state;
        this.toBeState = toBeState;
        this.expirationDate = expirationDate;
        this.alarmTime = alarmTime;
        this.oAuthProvider = oAuthProvider;
        this.reminiList = reminiList;
    }

    //구독 모델 변경을 위한 메서드
    public void standardToPremium(){
        this.state = State.PREMIUM;
        this.toBeState = State.PREMIUM;
    }
    public void premiumToStandard(){
        this.toBeState = State.STANDARD;
    }
    public void premiumExpired(){
        this.state = State.STANDARD;
        this.expirationDate = null;
    }
    public void initializeExpirationDate(){
        this.expirationDate = null;
    }
    public void setExpirationDate(){
        this.expirationDate = LocalDateTime.now().plusMonths(1L);
    }

    //알람 시간 설정,전송을 위한 메서드
    public void setAlarmTime(LocalDateTime alarmTime) {
        this.alarmTime = alarmTime;
    }
    public LocalDateTime getAlarmTime() {
        return alarmTime;
    }
}
