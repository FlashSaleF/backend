package com.flash.alarm.application.service.util;

import com.flash.alarm.application.dto.reqeuest.SchedulerRequestDto;
import com.flash.alarm.domain.model.ScheduledTaskEntity;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskMapper {

    public static ScheduledTaskEntity entityFrom(SchedulerRequestDto schedulerRequestDto) {
        return ScheduledTaskEntity.builder()
                .schedulingTime(schedulerRequestDto.flashSaleTime())
                .flashSaleProductId(schedulerRequestDto.flashSaleProductId())
                .flashSaleId(schedulerRequestDto.flashSaleId())
                .build();
    }
}
