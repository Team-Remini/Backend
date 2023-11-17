package com.likelion.remini.service;

import com.likelion.remini.domain.User;
import com.likelion.remini.jwt.AuthTokensGenerator;
import com.likelion.remini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    /* 알림 전송 api */
    //알림 설정 api
    @Transactional
    public void setAlarm(LocalDateTime alarmTime){
        Long userId = authTokensGenerator.extractMemberId();
        User user= userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        user.setAlarmTime(alarmTime);
        userRepository.save(user);
    }

    //알림 해제 api
    @Transactional
    public void cancelAlarm(){
        Long userId = authTokensGenerator.extractMemberId();
        User user= userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        user.setAlarmTime(null);
        userRepository.save(user);
    }

    //알림 체크 후 자동 발송 api
    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // 정각마다 실행
    public void checkAndSendAlarms(){
        List<User> userList = userRepository.findAll();
        LocalDateTime currentTime = LocalDateTime.now();
        for(User user : userList){
            LocalDateTime alarmTime = user.getAlarmTime();
            if(alarmTime != null && currentTime.isAfter(alarmTime)){
                String message = "좀 써라";
                sendAlarm(user, message);
                user.setAlarmTime(null);
                userRepository.save(user);
            }
        }
    }
    //알림 발송 api
    public void sendAlarm(User user, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("써라");
        message.setText(text);
        javaMailSender.send(message);
    }

    //테스트용 api
    public void testAlarm(){
        Long userId = authTokensGenerator.extractMemberId();
        User user= userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("써라");
        message.setText("제곧내");
        javaMailSender.send(message);
    }
}
