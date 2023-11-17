package com.likelion.remini.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlarmTimeRequestDTO {
    private LocalDateTime alarmTime;
}
