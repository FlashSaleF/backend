package com.flash.user.application.dto.request;

/**
 * Auth서비스에서 유효성 검사를 했기 때문에 여기서는 하지 않음.
 *
 * @param email
 * @param password
 */
public record LoginRequestDto(
        String email,
        String password
) {
}
