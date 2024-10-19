package com.flash.alarm.application.service.util;

import com.flash.alarm.application.dto.reqeuest.AlarmRequestDto;
import com.flash.alarm.application.dto.response.AlarmResponseDto;
import com.flash.alarm.application.dto.response.UserResponseDto;
import com.flash.alarm.domain.model.Alarm;
import org.springframework.stereotype.Component;

@Component
public class AlarmMapper {

    public static Alarm entityFrom(AlarmRequestDto alarmRequestDto, UserResponseDto userResponseDto) {
        return Alarm.builder()
                .userId(userResponseDto.id())
                .userEmail(userResponseDto.email())
                .flashSaleProductId(alarmRequestDto.flashSaleProductId())
                .flashSaleId(alarmRequestDto.flashSaleId())
                .flashSaleProductName(alarmRequestDto.flashSaleProductName())
                .build();
    }

    public static AlarmResponseDto fromEntity(Alarm alarm) {
        return AlarmResponseDto.builder()
                .id(alarm.getId())
                .title(alarm.getTitle())
                .contents(alarm.getContent())
                .userId(alarm.getUserId())
                .userEmail(alarm.getUserEmail())
                .flashSaleProductId(alarm.getFlashSaleProductId())
                .flashSaleProductName(alarm.getFlashSaleProductName())
                .flashSaleId(alarm.getFlashSaleId())
                .build();
    }
}
