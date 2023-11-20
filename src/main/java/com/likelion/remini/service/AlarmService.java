package com.likelion.remini.service;

import com.likelion.remini.domain.User;
import com.likelion.remini.exception.UserErrorResult;
import com.likelion.remini.exception.UserException;
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
    /**
     * 요청자의 알림을 설정하는 메서드이다.
     *
     * @param alarmTime 요청자가 설정한 알림 시간
     */
    @Transactional
    public void setAlarm(LocalDateTime alarmTime){
        Long userId = authTokensGenerator.extractMemberId();
        User user= userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        user.setAlarmTime(alarmTime);
        userRepository.save(user);
    }

    /**
     * 사용자의 알림을 해제하는 메서드이다.
     *
     */
    @Transactional
    public void cancelAlarm(){
        Long userId = authTokensGenerator.extractMemberId();
        User user= userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 userid로 찾을 수 없습니다 : " + userId));
        user.setAlarmTime(null);
        userRepository.save(user);
    }

    /**
     * 정각마다 알림시간 경과한 사용자에게 알림을 전송하고 알림시간을 초기화하는 메서드이다.
     *
     */
    @Transactional
    @Scheduled(cron = "0 0 * * * ?") // 정각마다 실행
    public void checkAndSendAlarms(){
        LocalDateTime currentTime = LocalDateTime.now();
        List<Long> userIdList = userRepository.findUserIdListAfterAlarm(currentTime);
        for(Long userId : userIdList){
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new UserException(UserErrorResult.USER_NOT_FOUND));
            String subject = "제목 : 설정 된 알림 발송";
            String message = "내용 : 설정 된 알림 발송";
            sendAlarm(user, subject, message);
            user.setAlarmTime(null);
            userRepository.save(user);
        }
    }

    /**
     * 구독 갱신 3일 전에 알림 메일을 전송하는 메서드이다.
     *
     */
    @Transactional
    @Scheduled(cron = "0 0 * * * *") // 정각마다 실행
    public void checkAndSendAlarmsForSubscribe(){
        LocalDateTime threeDayAfter = LocalDateTime.now().plusDays(3);
        List<Long> userIdList = userRepository.findUserIdListAfterExpiration(threeDayAfter);
        for(Long userId : userIdList){
            User user = userRepository.findById(userId)
                    .orElseThrow(()->new UserException(UserErrorResult.USER_NOT_FOUND));
            String subject = "제목 : 3일 뒤에 구독 갱신됩니다";
            String message = "내용 : 3일 뒤에 구독 갱신됩니다";
            sendAlarm(user, subject, message);
            user.setAlarmTime(null);
            userRepository.save(user);
        }
    }
    /**
     * 요청자에게 알람을 전송하는 메서드이다.
     *
     * @param user, subject, text 알람 대상자, 내용, 알람 메일 내용
     */
    public void sendAlarm(User user, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    /**
     * 알람을 테스트용 메서드이다.
     *
     */
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
