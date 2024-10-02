package com.flash.vendor.application.service;

import com.flash.vendor.application.dto.response.UserResponseDto;

public interface FeignClientService {
    UserResponseDto getUserInfo(String userId);
}
