package com.flash.alarm.presentation.controller;

import com.flash.alarm.application.dto.reqeuest.AlarmRequestDto;
import com.flash.alarm.application.dto.response.AlarmResponseDto;
import com.flash.alarm.application.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j(topic = "Alarm Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    /**
     * 알람을 설정하는 API 엔드포인트.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<UUID> setAlarm(@RequestBody AlarmRequestDto alarmRequestDto) {
        return ResponseEntity.ok(alarmService.setAlarm(alarmRequestDto));
    }

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/{alarmId}")
    public ResponseEntity<AlarmResponseDto> getAlarm(@PathVariable UUID alarmId) {
        return ResponseEntity.ok(alarmService.getAlarm(alarmId));
    }

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping
    public ResponseEntity<Page<AlarmResponseDto>> getAlarmList(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sortBy,
            @RequestParam("isAsc") boolean isAsc
    ) {
        return ResponseEntity.ok(alarmService.getAlarmList(page - 1, size, sortBy, isAsc));
    }

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/search")
    public ResponseEntity<Page<AlarmResponseDto>> searchAlarmList(
            @RequestParam("email") String userEmail,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sortBy,
            @RequestParam("isAsc") boolean isAsc
    ) {
        return ResponseEntity.ok(alarmService.searchAlarmList(userEmail, page - 1, size, sortBy, isAsc));
    }


}
