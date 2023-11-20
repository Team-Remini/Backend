package com.likelion.remini.controller;

import com.likelion.remini.dto.AlarmTimeRequestDTO;
import com.likelion.remini.service.AlarmService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alarm")
@RequiredArgsConstructor
@Api(tags = "Alarm", description = "알람 관련 API")
public class AlarmController {
    private final AlarmService alarmService;

    // 모든 사용자의 알람을 체크하고 메일을 발송
    @PostMapping("/checkAndSendAlarm")
    @ApiOperation(value = "사용자 알람 체크 후 자동 메일 발송")
    @ApiResponses({
            @ApiResponse(code = 200, message = "알람 전송 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<String> checkAndSendAlarm() {
        alarmService.checkAndSendAlarms();
        return ResponseEntity.status(HttpStatus.OK).body("알람 체크 및 발송이 완료되었습니다.");
    }
    // 갱신 3일 전 알림 메일을 발송
    @ApiOperation(value = "갱신 3일 전 알림 메일을 발송")
    @ApiResponses({
            @ApiResponse(code = 200, message = "알람 전송 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    @PostMapping("/checkAndSend")
    public ResponseEntity<String> checkAndSendAlarmsForSubscribe() {
        alarmService.checkAndSendAlarmsForSubscribe();
        return ResponseEntity.status(HttpStatus.OK).body("구독 3일 전 알람이 완료되었습니다.");
    }

    // 알람을 발송
    @ApiOperation(value = "메일을 발송")
    @ApiResponses({
            @ApiResponse(code = 200, message = "알람 전송 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    @PostMapping("/send")
    public ResponseEntity<String> testAlarm() {
        alarmService.testAlarm();
        return ResponseEntity.status(HttpStatus.OK).body("알람 체크 및 발송이 완료되었습니다.");
    }

    // 개별 사용자의 알람 시간을 설정
    @PatchMapping("/{userId}")
    @ApiOperation(value = "알람 설정")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "alarmRequestDTO", value="설정할 알람 정보", required = true, paramType = "body", dataTypeClass = AlarmTimeRequestDTO.class)
    })
    @ApiResponses({
            @ApiResponse(code = 201, message = "회고 생성 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    public ResponseEntity<String> setAlarm(
            @RequestBody AlarmTimeRequestDTO requestDTO) {
        alarmService.setAlarm(requestDTO.getAlarmTime());
        return ResponseEntity.status(HttpStatus.OK).body("알람 시간이 설정되었습니다.");
    }

    //알림 초기화(alarmtime null로)
    @ApiOperation(value = "알람 삭제")
    @ApiResponses({
            @ApiResponse(code = 204, message = "알람 삭제 성공"),
            @ApiResponse(code = 500, message = "서버 내 오류")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Long> deleteAlarm(){
        alarmService.cancelAlarm();
        return ResponseEntity.noContent().build();
    }


}
