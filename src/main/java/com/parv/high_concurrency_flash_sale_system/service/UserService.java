package com.parv.high_concurrency_flash_sale_system.service;

import com.parv.high_concurrency_flash_sale_system.entity.User;
import com.parv.high_concurrency_flash_sale_system.repository.UserRepository;
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

    public Boolean signUp (String username, String password,String email) {
        try{
            User user = new User();
            user.setUsername(username);
            user.setPassword(encoder.encode(password));
            user.setEmail(email);
            userRepository.save(user);
            return true;
        }catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }



    }

}
