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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String, String> context = new HashMap<>();
        Map<String, String> link = new HashMap<>();

        for(User user : userList){
            String subject = "[Remini 회고 알림] 이전에 작성한 회고를 돌아보세요!";
            Remini remini = reminiRepository.findFirstByUserOrderByModifiedDateDesc(user)
                    .orElseThrow(() -> new ReminiException(ReminiErrorResult.REMINI_NOT_FOUND));

            context.clear();
            context.put("title", remini.getTitle());
            context.put("type", remini.getType().getName());
            context.put("modifiedDate", remini.getModifiedDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));

            link.clear();
            link.put("reminiLink", "https://remini.vercel.app/complete-writing/"+remini.getReminiId());

            sendAlarm(user.getEmail(), subject, "templates/remind-alarm.html", context, link);
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

        Map<String, String> context = new HashMap<>();

        List<User> userList = userRepository.findByExpirationDateBetween(start, end);
        for(User user : userList){
            String subject = "[Remini 구독 결제 예정] Premium 구독 모델 결제 예정 안내";

            context.clear();
            context.put("state", user.getState().getName());
            context.put("nextPaymentDate", user.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
            context.put("cost", "9900원");

            sendAlarm(user.getEmail(), subject, "templates/payment-plan.html", context);
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

        Map<String, String> context = new HashMap<>();

        List<User> userList = userRepository.findByExpirationDateBetween(start, end);
        for(User user : userList){
            String subject = "[Remini 구독 결제 내역] Premium 구독 모델 결제 내역 안내";

            context.put("state", user.getState().getName());
            context.put("cost", "9900원");
            context.put("paymentDate", user.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh:mm")));
            context.put("expirationDate", user.getExpirationDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh:mm")));
            context.put("nextPaymentDate", user.getExpirationDate().plusMonths(1L).format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));

            sendAlarm(user.getEmail(), subject, "templates/payment-history.html", context);

            user.setExpirationDate();
            userRepository.save(user);
        }
    }

    /**
     * 주어진 HTML 파일을 가공한 다음, 메일로 보내는 메서드이다.
     *
     * @param recipient 수신자 이메일
     * @param subject 메일 제목
     * @param html 클래스패스 아래의 HTML 파일 경로 (e.g. templates/mail.html)
     * @param context HTML 파일에서 텍스트를 바꿀 element의 class-text 쌍
     */
    private void sendAlarm(String recipient, String subject, String html, Map<String, String> context) {
        String htmlText = processHtml(html, context, Collections.emptyMap());

        sendMail(recipient, subject, htmlText);
    }

    /**
     * 주어진 HTML 파일을 가공한 다음, 메일로 보내는 메서드이다.
     *
     * @param recipient 수신자 이메일
     * @param subject 메일 제목
     * @param html 클래스패스 아래의 HTML 파일 경로 (e.g. templates/mail.html)
     * @param context HTML 파일에서 텍스트를 바꿀 element의 class-text 쌍
     * @param link HTML 파일에서 연결할 주소를 바꿀 anchor의 class-href 쌍
     */
    private void sendAlarm(String recipient, String subject, String html, Map<String, String> context, Map<String, String> link) {
        String htmlText = processHtml(html, context, link);

        sendMail(recipient, subject, htmlText);
    }

    /**
     * JavaMailSender를 통해 HTML 메일을 보내는 메서드이다.
     *
     * @param recipient - 수신자 이메일
     * @param subject - 메일 제목
     * @param htmlText - 메일 내용
     */
    private void sendMail(String recipient, String subject, String htmlText) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(recipient);
            helper.setSubject(subject);

            helper.setText(htmlText, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * 주어진 HTML 파일을 가공하여 문자열로 반환하는 메서드이다.
     *
     * @param html 클래스패스 아래의 HTML 파일 경로 (e.g. templates/mail.html)
     * @param context HTML 파일에서 텍스트를 바꿀 element의 class-text 쌍
     * @param link HTML 파일에서 연결할 주소를 바꿀 anchor의 class-href 쌍
     * @return 가공된 HTML 문자열
     */
    private String processHtml(String html, Map<String, String> context, Map<String, String> link) {
        Document doc = getDocument(html);

        context.forEach((clazz, text) -> {
            Elements elements = doc.getElementsByClass(clazz);
            for (Element element : elements) {
                element.text(text);
            }
        });

        link.forEach((clazz, href) -> {
            Elements elements = doc.getElementsByClass(clazz);
            elements.attr("href", href);
        });

        return doc.html();
    }

    private Document getDocument(String html) {
        ClassPathResource resource = new ClassPathResource(html);
        File input = null;

        try {
            input = resource.getFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file.", e);
        }

        try {
            return Jsoup.parse(input, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse file", e);
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
