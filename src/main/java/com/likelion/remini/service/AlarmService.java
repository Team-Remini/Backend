package com.likelion.remini.service;

import com.likelion.remini.domain.Remini;
import com.likelion.remini.domain.State;
import com.likelion.remini.domain.Type;
import com.likelion.remini.domain.User;
import com.likelion.remini.exception.ReminiErrorResult;
import com.likelion.remini.exception.ReminiException;
import com.likelion.remini.jwt.AuthTokensGenerator;
import com.likelion.remini.repository.ReminiRepository;
import com.likelion.remini.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlarmService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final ReminiRepository reminiRepository;
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
        List<User> userList = userRepository.findUserListAfterAlarm(currentTime);
        for(User user : userList){
            String subject = "[Remini 회고 알림] 이전에 작성한 회고를 돌아보세요!";
            Remini remini = reminiRepository.findFirstByUserOrderByModifiedDateDesc(user)
                    .orElseThrow(() -> new ReminiException(ReminiErrorResult.REMINI_NOT_FOUND));
            sendAlarm_remini_alarm(user, subject, remini.getTitle(), remini.getType(), remini.getModifiedDate(), remini.getReminiId());
            user.setAlarmTime(null);
            userRepository.save(user);
        }
    }

    /**
     * 구독 갱신 3일 전에 알림 메일을 전송하는 메서드이다.
     *
     */
    @Transactional
    @Scheduled(cron = "0 0 9 * * *") // 매일 9시마다 실행
    public void checkAndSendAlarmsForSubscribe(){
        long daysLeft = 3L;

        LocalDate expirationDate = LocalDate.now().plusDays(daysLeft);
        LocalDateTime start = LocalDateTime.of(expirationDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(expirationDate, LocalTime.MAX);

        List<User> userList = userRepository.findByExpirationDateBetween(start, end);
        for(User user : userList){
            String subject = "[Remini 구독 결제 예정] Premium 구독 모델 결제 예정 안내";
            sendAlarm_payment_plan(user, subject, user.getState(), user.getExpirationDate(), "9900");
            userRepository.save(user);
        }
    }



    /**
     * 구독 갱신 알림 메일을 전송하는 메서드이다.
     *
     */
    @Transactional
    @Scheduled(cron = "0 0 9 * * *") // 매일 9시마다 실행
    public void checkAndSendAlarmsForSubscribed(){
        LocalDate expirationDate = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(expirationDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(expirationDate, LocalTime.MAX);

        List<User> userList = userRepository.findByExpirationDateBetween(start, end);
        for(User user : userList){
            String subject = "[Remini 구독 결제 내역] Premium 구독 모델 결제 내역 안내";
            sendAlarm_payment_history(user, subject, user.getState(), user.getExpirationDate(), user.getExpirationDate(), "9900",user.getExpirationDate().plusMonths(1L));
            user.setExpirationDate();
            userRepository.save(user);
        }
    }
    /**
     * 요청자에게 사용자 설정 알람을 전송하는 메서드이다.
     *
     * @param user, template, subject, text 알람 대상자, 내용, 알람 메일 내용
     */
    public void sendAlarm_remini_alarm(User user, String subject, String title, Type type, LocalDateTime modifiedDate, Long reminiId) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("title", title);
            context.setVariable("type", type);
            context.setVariable("modifiedDate", modifiedDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
            context.setVariable("reminiId", reminiId);

            String htmlContent = templateEngine.process("remind-alarm.html", context);

            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendAlarm_payment_plan(User user, String subject, State state, LocalDateTime nextPaymentDate, String cost) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("state", state);
            context.setVariable("nextPaymentDate", nextPaymentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
            context.setVariable("cost", cost);
            String htmlContent = templateEngine.process("payment-plan.html", context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendAlarm_payment_history(User user, String subject, State state, LocalDateTime paymentDate, LocalDateTime expirationDate, String cost, LocalDateTime nextPaymentDate) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("state", state);
            context.setVariable("paymentDate", paymentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh:mm")));
            context.setVariable("expirationDate", expirationDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh:mm")));
            context.setVariable("cost", cost);
            context.setVariable("nextPaymentDate", nextPaymentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));

            String htmlContent = templateEngine.process("payment-history.html", context);
            helper.setText(htmlContent, true); // HTML 이메일 설정

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
