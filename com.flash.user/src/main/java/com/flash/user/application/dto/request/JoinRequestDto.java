package com.flash.user.application.dto.request;

import lombok.With;

/**
 * Auth서비스에서 유효성 검사를 했기 때문에 여기서는 하지 않음.
 *
 * @param email
 * @param password
 * @param role
 * @param address
 * @param phone
 * @param name
 */
public record JoinRequestDto(
        String email,
        @With String password,
        String role,
        String address,
        String phone,
        String name
) {
}

