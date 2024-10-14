package com.flash.user.application.dto.response;

import lombok.Builder;

/**
 * Vendor서비스에서의 FeignClient요청에 대한 반환 값을 정의한 dto
 *
 * 추가로 유효한 유저인지 요청할 때 반환값으로 사용.
 *
 * @param name
 */
@Builder
public record UserResponseDto(
        String name,
        String email,
        Long id
) {
}
