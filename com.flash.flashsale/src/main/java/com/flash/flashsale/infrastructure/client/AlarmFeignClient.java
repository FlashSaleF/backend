package com.flash.flashsale.infrastructure.client;

import com.flash.flashsale.application.dto.request.SchedulerRequestDto;
import com.flash.flashsale.infrastructure.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "alarm", configuration = FeignConfig.class)
public interface AlarmFeignClient {
    @PostMapping("/api/internal/alarms")
    Long scheduleAlarm(@RequestBody SchedulerRequestDto schedulerRequestDto);
}
