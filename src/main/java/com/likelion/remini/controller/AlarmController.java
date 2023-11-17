package com.likelion.remini.controller;

import com.likelion.remini.dto.AlarmTimeRequestDTO;
import com.likelion.remini.service.AlarmService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
@Api(tags = "Alarm", description = "알람 관련 API")
public class AlarmController {
    private final AlarmService alarmService;

    // 모든 사용자의 알람을 체크하고 알람을 발송
    @PostMapping("/checkAndSend")
    public ResponseEntity<String> checkAndSendAlarms() {
        alarmService.checkAndSendAlarms();
        return ResponseEntity.status(HttpStatus.OK).body("알람 체크 및 발송이 완료되었습니다.");
    }

    // 알람을 발송
    @PostMapping("/send")
    public ResponseEntity<String> testAlarm() {
        alarmService.testAlarm();
        return ResponseEntity.status(HttpStatus.OK).body("알람 체크 및 발송이 완료되었습니다.");
    }

    // 개별 사용자의 알람 시간을 설정
    @PatchMapping("/{userId}")
    public ResponseEntity<String> setAlarm(
            @RequestBody AlarmTimeRequestDTO requestDTO) {
        alarmService.setAlarm(requestDTO.getAlarmTime());
        return ResponseEntity.status(HttpStatus.OK).body("알람 시간이 설정되었습니다.");
    }

    //알림 초기화(alarmtime null로)
    @DeleteMapping("/{userId}")
    public ResponseEntity<Long> deleteAlarm(){
        alarmService.cancelAlarm();
        return ResponseEntity.noContent().build();
    }


}
