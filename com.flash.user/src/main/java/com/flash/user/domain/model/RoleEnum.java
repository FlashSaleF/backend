package com.flash.user.domain.model;

import com.flash.base.exception.CustomException;
import com.flash.user.domain.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleEnum {

    MASTER(Authority.MASTER),
    MANAGER(Authority.MANAGER),
    VENDOR(Authority.VENDOR),
    CUSTOMER(Authority.CUSTOMER);

    private final String authority;

    /**
     * roleCode에 따른 RoleEnum을 반환
     *
     * @param roleCode 전달 받은 권한(Role_권한)형식
     * @return RoleEnum
     */
    public static RoleEnum fromRoleCode(String roleCode) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.authority.equalsIgnoreCase(roleCode)) {
                return role;
            }
        }
        throw new CustomException(UserErrorCode.INVALID_ROLE);
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String MASTER = "ROLE_MASTER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String VENDOR = "ROLE_VENDOR";
        public static final String CUSTOMER = "ROLE_CUSTOMER";
    }

}

