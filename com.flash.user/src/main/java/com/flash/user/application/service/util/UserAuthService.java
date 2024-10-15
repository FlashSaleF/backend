package com.flash.user.application.service.util;

import com.flash.base.exception.CustomException;
import com.flash.user.domain.exception.UserErrorCode;
import com.flash.user.domain.model.RoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j(topic = "User Auth Service")
@Component
@RequiredArgsConstructor
public class UserAuthService {

    public void verifyIdentity(String userId) {
        // 현재 인증된 사용자의 정보를 가져옴
        String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 인증된 사용자의 ID와 전달된 userId가 같은지 확인
        if (userId.equals(authenticatedUserId)) {
            return;
        }

        // 다르면 권한이 MASTER인지 확인
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(RoleEnum.MASTER)) {
                return;  // MASTER 권한이면 return
            }
        }
        // userId가 일치하지 않고, 권한도 MASTER가 아닌 경우 예외처리
        log.error("access denied");
        throw new CustomException(UserErrorCode.ACCESS_DENIED);
    }

}
