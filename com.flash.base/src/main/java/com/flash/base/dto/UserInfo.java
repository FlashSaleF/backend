package com.flash.base.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;

@JsonSerialize
@JsonDeserialize
@Builder
public record UserInfo(
        String id,
        String role,
        String token
) {
}
