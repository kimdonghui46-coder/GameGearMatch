package com.gamegearmatch.backend.user.dto;

import com.gamegearmatch.backend.user.domain.Role;
import com.gamegearmatch.backend.user.domain.User;

public record SignupResponse(
        Long id,
        String email,
        String name,
        Role role
) {

    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}