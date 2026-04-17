package com.parv.high_concurrency_flash_sale_system.service;

import com.parv.high_concurrency_flash_sale_system.entity.User;
import com.parv.high_concurrency_flash_sale_system.repository.UserRepository;
import com.parv.high_concurrency_flash_sale_system.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder encoder;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public String signUp (User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);
        return jwtUtil.generateToken(user.getUsername());
    }

    public String logIn (User user) {
        User savedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        boolean isPasswordCorrect = encoder.matches(user.getPassword(), savedUser.getPassword());

        if (!isPasswordCorrect) {
            throw new RuntimeException("Invalid username or password");
        }

        return jwtUtil.generateToken(savedUser.getUsername());


    }


}
