package com.parv.high_concurrency_flash_sale_system.controller;

import com.parv.high_concurrency_flash_sale_system.entity.User;
import com.parv.high_concurrency_flash_sale_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody User user) {
        String jwt = userService.signUp(user);
        if (jwt != null) {
            user.setPassword(null);
            return ResponseEntity.ok().header("Authorization", "Bearer " + jwt).body(user);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        String jwt = userService.logIn(user);
        if (jwt != null) {
            user.setPassword(null);
            return ResponseEntity.ok().header("Authorization", "Bearer " + jwt).body(user);
        }
        return ResponseEntity.badRequest().build();
    }
}
