package com.flash.user.presentation.controller;

import com.flash.user.application.dto.request.UpdateRequestDto;
import com.flash.user.application.dto.response.UserInfoResponseDto;
import com.flash.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "User Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/self")
    public ResponseEntity<UserInfoResponseDto> getUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping
    public ResponseEntity<UserInfoResponseDto> updateUser(@RequestBody UpdateRequestDto updateRequestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(userService.updateUser(userId, updateRequestDto));
    }
}
