package com.gamegearmatch.backend.user.dto;

import com.gamegearmatch.backend.user.domain.Role;
import com.gamegearmatch.backend.user.domain.User;

public record CurrentUserResponse(
        Long id,
        String email,
        String name,
        Role role
) {

    public static CurrentUserResponse from(User user) {
        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );
    }
}