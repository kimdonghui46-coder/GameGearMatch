package com.gamegearmatch.backend.user.service;

import com.gamegearmatch.backend.security.jwt.JwtTokenProvider;
import com.gamegearmatch.backend.user.domain.Role;
import com.gamegearmatch.backend.user.domain.User;
import com.gamegearmatch.backend.user.dto.LoginRequest;
import com.gamegearmatch.backend.user.dto.LoginResponse;
import com.gamegearmatch.backend.user.dto.SignupRequest;
import com.gamegearmatch.backend.user.dto.SignupResponse;
import com.gamegearmatch.backend.user.repository.UserRepository;
import com.gamegearmatch.backend.user.dto.CurrentUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String normalizedEmail =
                request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 이메일입니다."
            );
        }

        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .name(request.name().trim())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return SignupResponse.from(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail =
                request.email().trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "이메일 또는 비밀번호가 올바르지 않습니다."
                ));

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        String accessToken =
                jwtTokenProvider.createAccessToken(
                        user.getEmail(),
                        user.getRole()
                );

        return LoginResponse.of(accessToken);
    }
    public CurrentUserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "사용자를 찾을 수 없습니다."
                ));

        return CurrentUserResponse.from(user);
    }
}