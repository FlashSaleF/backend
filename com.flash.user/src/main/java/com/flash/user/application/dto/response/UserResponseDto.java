package com.flash.user.application.dto.response;

import lombok.Builder;

/**
 * Vendor서비스에서의 FeignClient요청에 대한 반환 값을 정의한 dto
 *
 * @param name
 */
@Builder
public record UserResponseDto(
        String name
) {
}
