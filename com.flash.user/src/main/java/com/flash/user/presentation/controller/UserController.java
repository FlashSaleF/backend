package com.flash.user.presentation.controller;

import com.flash.user.application.dto.request.UpdateRequestDto;
import com.flash.user.application.dto.response.UserInfoResponseDto;
import com.flash.user.application.dto.response.UserResponseDto;
import com.flash.user.application.service.UserService;
import com.flash.user.application.service.util.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j(topic = "User Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDto> getUser(@PathVariable String userId) {
        userAuthService.verifyIdentity(userId);
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserInfoResponseDto> updateUserByMaster(@PathVariable String userId, @RequestBody UpdateRequestDto updateRequestDto) {
        userAuthService.verifyIdentity(userId);
        return ResponseEntity.ok(userService.updateUser(userId, updateRequestDto));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable String userId) {
        userAuthService.verifyIdentity(userId);
        return ResponseEntity.ok(userService.deleteUser(userId));
    }
}
