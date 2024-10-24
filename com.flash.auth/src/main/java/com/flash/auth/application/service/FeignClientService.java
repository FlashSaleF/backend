package com.flash.auth.application.service;

import com.flash.auth.application.dto.request.JoinRequestDto;
import com.flash.auth.application.dto.request.LoginRequestDto;
import com.flash.auth.application.dto.response.JoinResponseDto;
import com.flash.auth.application.dto.response.LoginResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public interface FeignClientService {

    JoinResponseDto saveUser(@RequestBody JoinRequestDto joinRequestDto);

    LoginResponseDto verifyUserCredentials(@RequestBody LoginRequestDto loginRequestDto);

}
