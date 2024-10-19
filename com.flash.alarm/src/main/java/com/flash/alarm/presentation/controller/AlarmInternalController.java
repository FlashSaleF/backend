package com.flash.alarm.presentation.controller;

import com.flash.alarm.application.dto.reqeuest.SchedulerRequestDto;
import com.flash.alarm.application.service.DynamicSchedulerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j(topic = "Alarm Internal Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/alarms")
public class AlarmInternalController {

    private final DynamicSchedulerService dynamicSchedulerService;

    @PostMapping
    public ResponseEntity<Long> scheduleAlarm(@Valid @RequestBody SchedulerRequestDto requestDto) {
        return ResponseEntity.ok(dynamicSchedulerService.scheduleTaskByFlashSaleTime(requestDto));
    }
}
